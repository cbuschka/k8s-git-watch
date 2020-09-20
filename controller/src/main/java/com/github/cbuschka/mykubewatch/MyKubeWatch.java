package com.github.cbuschka.mykubewatch;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1ConfigMapList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyKubeWatch
{
	public void run() throws IOException, ApiException
	{
		ApiClient client = Config.defaultClient();
		client.getHttpClient().setReadTimeout(0, TimeUnit.MILLISECONDS);
		Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api();

		MyKubeWatchConfig config = loadConfig(api);


		Watch<?> watch = null;
		try
		{
			watch = Watch.createWatch(
					client,
					api.listPodForAllNamespacesCall(null, null, null, null, 5, null, null, null, Boolean.TRUE, null, null),
					new TypeToken<Watch.Response<?>>()
					{
					}.getType());
			final Watch<?> innerWatch = watch;
			Runnable watchTask = new Runnable()
			{
				@Override
				public void run()
				{
					innerWatch.forEach(response -> {
						EventHandler eventHandler = getEventsHandlerFor(config);
						KObject kObject = KObject.wrap(response.object);
						String kind = kObject.getString("kind");
						String name = kObject.get("metadata").getString("name");
						String namespace = kObject.get("metadata").getString("namespace");
						if (isIncluded(response.type, kind, namespace, name, kObject.asMap(), config))
						{
							eventHandler.handle(response.type, kind, namespace, name, kObject.asMap());
						}
					});
				}
			};
			// new Thread(watchTask).start();
			watchTask.run();
		}
		finally
		{
			if (watch != null)
			{
				watch.close();
			}
		}
	}

	private boolean isIncluded(String type, String kind, String namespace, String name, Map<String, Object> data, MyKubeWatchConfig config)
	{
		if (config == null || config.filter == null)
		{
			return true;
		}

		MyKubeWatchConfig.Filter filter = config.filter;
		if (filter.namespaces != null && !filter.namespaces.contains(namespace))
		{
			return false;
		}

		if (filter.kinds != null && !filter.kinds.contains(kind))
		{
			return false;
		}

		if (filter.names != null && !filter.names.contains(name))
		{
			return false;
		}

		return true;
	}

	private EventHandler getEventsHandlerFor(MyKubeWatchConfig config)
	{
		CompositeEventHandler compositeEventHandler = new CompositeEventHandler();

		if (config != null)
		{
			MyKubeWatchConfig.Mattermost mattermost = config.handler.mattermost;
			compositeEventHandler.addDelegate(new MmEventHandler(new MmClient(mattermost.url, mattermost.channel, mattermost.username)));
		}

		compositeEventHandler.addDelegate(new ConsoleEventHandler());

		return compositeEventHandler;
	}

	private MyKubeWatchConfig loadConfig(CoreV1Api api) throws ApiException
	{
		V1ConfigMapList v1ConfigMapList = api.listConfigMapForAllNamespaces(null, "metadata.name=mykubewatch", null, null, null, null, null, null, null);
		for (V1ConfigMap v1ConfigMap : v1ConfigMapList.getItems())
		{
			Yaml yaml = new Yaml();
			MyKubeWatchConfig config = yaml.loadAs(v1ConfigMap.getData().get(".mykubewatch.yaml"),
					MyKubeWatchConfig.class);
			return config;
		}

		return MyKubeWatchConfig.defaultConfig();
	}
}
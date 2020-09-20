package com.github.cbuschka.git_watch.controller;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryWatchWatcher implements Runnable
{
	private static Logger log = LoggerFactory.getLogger(RepositoryWatchWatcher.class);

	private RepositoryRegistry repositoryRegistry;

	public RepositoryWatchWatcher(RepositoryRegistry repositoryRegistry)
	{
		this.repositoryRegistry = repositoryRegistry;
	}

	public void run()
	{
		try
		{
			ApiClient client = Config.defaultClient();
			client.setReadTimeout(0);
			OkHttpClient httpClient = client.getHttpClient();
			client.setHttpClient(httpClient);

			CustomObjectsApi customObjectsApi = new CustomObjectsApi();
			customObjectsApi.setApiClient(client);

			try (Watch<Object> watch = Watch.createWatch(
					client,
					customObjectsApi.listNamespacedCustomObjectCall(
							"gitwatch.cbuschka.github.io", "v1alpha1", "", "repositories", null, null, null, null, null, null, null, Boolean.TRUE, null),
					new TypeToken<Watch.Response<Object>>()
					{
					}.getType());)
			{
				log.info("Running watch...");
				watch.forEach(response -> {
					KObject kObject = KObject.wrap(response.object);
					String kind = kObject.getString("kind");
					String name = kObject.get("metadata").getString("name");
					String namespace = kObject.get("metadata").getString("namespace");
					String url = kObject.get("spec").getString("url");
					log.info("Seen kind={} name={} namespace={} url={}", kind, name, namespace, url);
				});
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
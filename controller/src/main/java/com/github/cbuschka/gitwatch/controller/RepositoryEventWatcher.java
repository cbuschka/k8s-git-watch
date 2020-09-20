package com.github.cbuschka.gitwatch.controller;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RepositoryEventWatcher implements Runnable
{
	private static final Logger log = LoggerFactory.getLogger(RepositoryEventWatcher.class);

	private final RepositoryEventHandler eventHandler;

	public RepositoryEventWatcher(RepositoryEventHandler eventHandler)
	{
		this.eventHandler = eventHandler;
	}

	public void run()
	{
		try
		{
			ApiClient client = createApiClient();
			CustomObjectsApi customObjectsApi = createCustomObjectsApi(client);

			try (Watch<Object> watch = createWatch(client, customObjectsApi);)
			{
				log.trace("Running watch...");
				watch.forEach(response -> {
					KObject kObject = KObject.wrap(response.object);
					String kind = kObject.getString("kind");
					String name = kObject.get("metadata").getString("name");
					String namespace = kObject.get("metadata").getString("namespace");
					log.debug("Seen kind={} name={} namespace={}", kind, name, namespace);

					if ("ADDED".equals(response.type))
					{
						this.eventHandler.repositoryAdded(namespace, name, kObject);
					}
					else if ("MODIFIED".equals(response.type))
					{
						this.eventHandler.repositoryModified(namespace, name, kObject);
					}
					else if ("DELETED".equals(response.type))
					{
						this.eventHandler.repositoryDeleted(namespace, name, kObject);
					}
				});
				log.trace("Watch finished.");
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException("Exception while watching.", ex);
		}
	}

	private Watch<Object> createWatch(ApiClient client, CustomObjectsApi customObjectsApi) throws io.kubernetes.client.openapi.ApiException
	{
		return Watch.createWatch(
				client,
				customObjectsApi.listNamespacedCustomObjectCall(
						Controller.GROUP, Controller.VERSION, "", Controller.REPOSITORY_PLURAL, null, null, null, null, null, null, null, Boolean.TRUE, null),
				new TypeToken<Watch.Response<Object>>()
				{
				}.getType());
	}

	private CustomObjectsApi createCustomObjectsApi(ApiClient client)
	{
		CustomObjectsApi customObjectsApi = new CustomObjectsApi();
		customObjectsApi.setApiClient(client);
		return customObjectsApi;
	}

	private ApiClient createApiClient() throws IOException
	{
		ApiClient client = Config.defaultClient();
		client.setReadTimeout(0);
		OkHttpClient httpClient = client.getHttpClient();
		client.setHttpClient(httpClient);
		return client;
	}
}
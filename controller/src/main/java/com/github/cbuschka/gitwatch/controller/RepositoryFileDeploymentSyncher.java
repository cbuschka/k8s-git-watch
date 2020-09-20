package com.github.cbuschka.gitwatch.controller;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Config;
import okhttp3.OkHttpClient;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class RepositoryFileDeploymentSyncher implements GitRepositoryEventHandler
{
	private static final Logger log = LoggerFactory.getLogger(RepositoryFileDeploymentSyncher.class);

	private final Yaml yaml = new Yaml();

	private final DeploymentStateMap deploymentStateMap;

	private final Md5Calculator md5Calculator = new Md5Calculator();

	public RepositoryFileDeploymentSyncher(DeploymentStateMap deploymentStateMap)
	{
		this.deploymentStateMap = deploymentStateMap;
	}

	@Override
	public void deploymentYmlFileFound(RepositoryFile repoFile)
	{
		try
		{
			byte[] bytes = IOUtils.toByteArray(new FileInputStream(repoFile.ymlFile));
			BigInteger hashCode = md5Calculator.hashCode(bytes);
			boolean uptodate = deploymentStateMap.isUptodate(repoFile.ymlFile.getCanonicalPath(), hashCode);

			if (uptodate)
			{
				log.debug("{} is uptodate.", repoFile.ymlFile.getCanonicalPath());
			}
			else
			{
				Map<String, Object> data = (Map<String, Object>) yaml.load(new ByteArrayInputStream(bytes));
				if ("Deployment".equals(data.get("kind")))
				{
					deploy(repoFile, data);

					deploymentStateMap.update(repoFile.ymlFile.getCanonicalPath(), hashCode);
				}
			}
		}
		catch (IOException | ApiException ex)
		{
			throw new RuntimeException("Synch failed.", ex);
		}
	}

	private ApiClient createApiClient() throws IOException
	{
		ApiClient client = Config.defaultClient();
		client.setReadTimeout(0);
		OkHttpClient httpClient = client.getHttpClient();
		client.setHttpClient(httpClient);
		return client;
	}

	private void deploy(RepositoryFile repoFile, Map<String, Object> data) throws ApiException, IOException
	{
		String namespace = (String) data.getOrDefault("namespace", "default");
		Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
		String name = (String) metadata.get("name");

		ApiClient apiClient = createApiClient();
		AppsV1Api api = new AppsV1Api(apiClient);

		V1Deployment newDeployment = toDeployment(data, apiClient);

		boolean deploymentExists = existsDeploymentFor(namespace, name, api);
		if (deploymentExists)
		{
			api.createNamespacedDeployment(namespace, newDeployment, null, null, null);
			log.info("Deployment namespace={}, name={} created from {}.", namespace, name, repoFile);
		}
		else
		{
			api.replaceNamespacedDeployment(name, namespace, newDeployment, null, null, null);
			log.info("Deployment namespace={}, name={} replaced from {}.", namespace, name, repoFile);
		}
	}

	private V1Deployment toDeployment(Map<String, Object> data, ApiClient apiClient)
	{
		String json = apiClient.getJSON().serialize(data);
		return apiClient.getJSON().deserialize(json, V1Deployment.class);
	}

	private boolean existsDeploymentFor(String namespace, String name, AppsV1Api api) throws ApiException
	{
		List<V1Deployment> deployments;
		if (namespace == null)
		{
			deployments = api.listDeploymentForAllNamespaces(Boolean.FALSE, null, String.format("metadata.name=%s", name), null, null, null, null, null, Boolean.FALSE).getItems();
		}
		else
		{
			deployments = api.listNamespacedDeployment(namespace, null, Boolean.FALSE, null, String.format("metadata.name=%s", name), null, null, null, null, Boolean.FALSE).getItems();
		}

		return deployments.isEmpty();
	}

}

package com.github.cbuschka.git_watch.controller;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Config;
import okhttp3.OkHttpClient;
import org.apache.commons.compress.utils.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RepositoryWatcher implements Runnable
{
	private static Logger log = LoggerFactory.getLogger(RepositoryWatcher.class);
	private final ApiClient client;

	private RepositoryRegistry repositoryRegistry;

	private File baseDir = new File("/tmp/workspaces/");

	public RepositoryWatcher(RepositoryRegistry repositoryRegistry) throws IOException
	{
		this.repositoryRegistry = repositoryRegistry;

		ApiClient client = Config.defaultClient();
		client.setReadTimeout(0);
		OkHttpClient httpClient = client.getHttpClient();
		client.setHttpClient(httpClient);
		this.client = client;
	}

	public void run()
	{

		while (true)
		{
			baseDir.mkdirs();

			Set<Repository> repositories = this.repositoryRegistry.snapshot();
			for (Repository repository : repositories)
			{
				log.info("Checking {}...", repository.name);
				Git git = null;
				try
				{
					File repoDir = new File(this.baseDir, repository.name);

					if (repoDir.isDirectory())
					{
						git = Git.open(repoDir);
						git.fetch().call();
						git.checkout().setCreateBranch(false).setName(repository.branch).call();
						git.reset().setMode(ResetCommand.ResetType.HARD).setRef("remotes/origin/" + repository.branch).call();
					}
					else
					{
						git = Git.cloneRepository()
								.setRemote("origin")
								.setURI(repository.remote)
								.setCloneAllBranches(true)
								.setDirectory(repoDir)
								.call();
						git.checkout().setCreateBranch(false).setName(repository.branch).call();
						git.reset().setMode(ResetCommand.ResetType.HARD).setRef("remotes/origin/" + repository.branch).call();
					}

					Set<File> ymlFiles = collectYmlFiles(repoDir);
					log.info("Found {}.", ymlFiles);

					for (File ymlFile : ymlFiles)
					{
						byte[] bytes = IOUtils.toByteArray(new FileInputStream(ymlFile));
						Yaml yaml = new Yaml();
						Map<String, Object> data = (Map) yaml.load(new ByteArrayInputStream(bytes));
						if ("Deployment".equals(data.get("kind")))
						{
							log.info("Good:  {} is a deployment.", ymlFile);
							deploy(ymlFile, data, bytes);
						}
					}

				}
				catch (Exception ex)
				{
					log.error("Failed.", ex);
				}
				finally
				{
					if (git != null)
					{
						git.close();
					}
				}
			}


			try
			{
				Thread.sleep(1000 * 10);
			}
			catch (InterruptedException ex)
			{
				break;
			}
		}


	}

	private void deploy(File ymlFile, Map<String, Object> data, byte[] bytes) throws ApiException
	{
		String namespace = (String) data.getOrDefault("namespace", "default");
		Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
		String name = (String) metadata.get("name");

		AppsV1Api api = new AppsV1Api(this.client);

		String json = this.client.getJSON().serialize(data);
		V1Deployment newDeployment = this.client.getJSON().deserialize(json, V1Deployment.class);

		List<V1Deployment> deployments;
		if (namespace == null)
		{
			deployments = api.listDeploymentForAllNamespaces(Boolean.FALSE, null, String.format("metadata.name=%s", name), null, null, null, null, null, Boolean.FALSE).getItems();
		}
		else
		{
			deployments = api.listNamespacedDeployment(namespace, null, Boolean.FALSE, null, String.format("metadata.name=%s", name), null, null, null, null, Boolean.FALSE).getItems();
		}

		if (deployments.isEmpty())
		{
			log.info("Deploying namespace={}, name={}...", namespace, name);
			api.createNamespacedDeployment(namespace, newDeployment, null, null, null);
		}
		else
		{
			log.info("Replacing namespace={}, name={}...", namespace, name);
			api.replaceNamespacedDeployment(name, namespace, newDeployment, null, null, null);
		}
	}

	private Set<File> collectYmlFiles(File repoDir)
	{
		try
		{
			Set<File> ymlFiles = new HashSet<>();
			collectYmlFilesInto(repoDir, repoDir, ymlFiles);
			return ymlFiles;
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private void collectYmlFilesInto(File dir, File repoDir, Set<File> ymlFiles) throws IOException
	{

		File[] files = dir.listFiles();
		if (files != null)
		{
			for (File file : files)
			{
				if (file.isDirectory() && !file.getName().equals(".git"))
				{
					collectYmlFilesInto(file, repoDir, ymlFiles);
				}
				else if (file.isFile() && (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")))
				{
					// ymlFiles.add(file.getCanonicalPath().substring(repoDir.getCanonicalPath().length()));
					ymlFiles.add(file);
				}
			}
		}
	}
}

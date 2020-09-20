package com.github.cbuschka.gitwatch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Controller
{
	private static final Logger log = LoggerFactory.getLogger(Controller.class);

	public static final String GROUP = "gitwatch.cbuschka.github.io";
	public static final String VERSION = "v1alpha1";
	public static final String REPOSITORY_PLURAL = "repositories";

	public void run() throws InterruptedException, IOException
	{
		RepositoryRegistry repositoryRegistry = new RepositoryRegistry();
		DeploymentStateMap deploymentStateMap = new DeploymentStateMap();
		RepositoryEventHandler repositoryEventHandler = new RepositoryRegistryEventHandler(repositoryRegistry);

		Thread repoWatchWatcher = startRepositoryEventWatcher(repositoryEventHandler);
		Thread repoWatcher = startGitRepositoryWatcher(repositoryRegistry, deploymentStateMap);

		waitFor(repoWatchWatcher, repoWatcher);
	}

	private void waitFor(Thread repoWatchWatcher, Thread repoWatcher) throws InterruptedException
	{
		repoWatchWatcher.join();
		repoWatcher.join();

		log.info("Watchers finished.");
	}

	private Thread startGitRepositoryWatcher(RepositoryRegistry repositoryRegistry, DeploymentStateMap deploymentStateMap) throws IOException
	{
		GitRepositoryWatcher repositoryWatcher = new GitRepositoryWatcher(repositoryRegistry, deploymentStateMap, new RepositoryFileDeploymentSyncher(deploymentStateMap));
		Thread repoWatcher = new Thread(repositoryWatcher, "GitRepositoryWatcher");
		repoWatcher.start();

		log.info("GitRepository watcher started.");

		return repoWatcher;
	}

	private Thread startRepositoryEventWatcher(RepositoryEventHandler repositoryEventHandler)
	{
		RepositoryEventWatcher watcher = new RepositoryEventWatcher(repositoryEventHandler);
		Thread repoWatchWatcher = new Thread(watcher, "RepositoryEventWatcher");
		repoWatchWatcher.start();

		log.info("Repository watcher started.");

		return repoWatchWatcher;
	}

}
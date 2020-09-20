package com.github.cbuschka.git_watch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Controller
{
	private static Logger log = LoggerFactory.getLogger(Controller.class);

	public void run() throws InterruptedException, IOException
	{
		RepositoryRegistry repositoryRegistry = new RepositoryRegistry();
		DeploymentStateMap deploymentStateMap = new DeploymentStateMap();

		RepositoryWatchWatcher watcher = new RepositoryWatchWatcher(repositoryRegistry);
		Thread repoWatchWatcher = new Thread(watcher, "RepoWatchWatcher");
		repoWatchWatcher.start();

		RepositoryWatcher repositoryWatcher = new RepositoryWatcher(repositoryRegistry, deploymentStateMap);
		Thread repoWatcher = new Thread(repositoryWatcher, "RepoWatcher");
		repoWatcher.start();


		repoWatchWatcher.join();
		repoWatcher.join();
	}
}
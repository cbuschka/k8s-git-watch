package com.github.cbuschka.gitwatch.controller;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.github.cbuschka.gitwatch.controller.Sleeping.sleepQuietly;

public class GitRepositoryWatcher implements Runnable
{
	private static final Logger log = LoggerFactory.getLogger(GitRepositoryWatcher.class);
	public static final int TEN_SECONDS_IN_MILLIS = 1000 * 10;

	private final RepositoryRegistry repositoryRegistry;

	private final File baseDir = new File("/tmp/workspaces/");

	private final GitRepositoryEventHandler gitRepositoryEventHandler;

	private final YmlFileCollector ymlFileCollector = new YmlFileCollector();

	public GitRepositoryWatcher(RepositoryRegistry repositoryRegistry, DeploymentStateMap deploymentStateMap, GitRepositoryEventHandler gitRepositoryEventHandler) throws IOException
	{
		this.repositoryRegistry = repositoryRegistry;
		this.gitRepositoryEventHandler = gitRepositoryEventHandler;
	}

	public void run()
	{
		if (!baseDir.mkdirs())
		{
			throw new IllegalStateException("Could not create base dir: " + this.baseDir);
		}

		while (!Thread.interrupted())
		{
			Set<Repository> repositories = this.repositoryRegistry.snapshot();
			for (Repository repository : repositories)
			{
				checkForChanges(repository);
			}

			sleepQuietly(TEN_SECONDS_IN_MILLIS);
		}
	}

	private void checkForChanges(Repository repository)
	{
		log.debug("Checking {}...", repository.name);

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
						.setURI(repository.uri)
						.setCloneAllBranches(true)
						.setDirectory(repoDir)
						.call();
				git.checkout().setCreateBranch(false).setName(repository.branch).call();
				git.reset().setMode(ResetCommand.ResetType.HARD).setRef("remotes/origin/" + repository.branch).call();
			}

			Set<File> ymlFiles = this.ymlFileCollector.collectYmlFiles(repoDir);
			for (File ymlFile : ymlFiles)
			{
				this.gitRepositoryEventHandler.deploymentYmlFileFound(new RepositoryFile(repository, ymlFile, repoDir));
			}
		}
		catch (Exception ex)
		{
			log.error("Checking repo {} for changes failed.", repository.name, ex);
		}
		finally
		{
			if (git != null)
			{
				git.close();
			}
		}
	}
}

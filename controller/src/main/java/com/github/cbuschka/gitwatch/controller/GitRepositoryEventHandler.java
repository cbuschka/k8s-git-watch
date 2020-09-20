package com.github.cbuschka.gitwatch.controller;

public interface GitRepositoryEventHandler
{
	void deploymentYmlFileFound(RepositoryFile repoFile);
}

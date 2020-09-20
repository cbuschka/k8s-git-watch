package com.github.cbuschka.gitwatch.controller;

public interface RepositoryEventHandler
{
	void repositoryAdded(String namespace, String name, KObject data);

	void repositoryModified(String namespace, String name, KObject data);

	void repositoryDeleted(String namespace, String name, KObject data);
}

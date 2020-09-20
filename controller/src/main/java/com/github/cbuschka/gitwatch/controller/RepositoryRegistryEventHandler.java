package com.github.cbuschka.gitwatch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryRegistryEventHandler implements RepositoryEventHandler
{
	private static final Logger log = LoggerFactory.getLogger(RepositoryRegistryEventHandler.class);

	private final RepositoryRegistry repositoryRegistry;

	public RepositoryRegistryEventHandler(RepositoryRegistry repositoryRegistry)
	{
		this.repositoryRegistry = repositoryRegistry;
	}

	@Override
	public void repositoryAdded(String namespace, String name, KObject object)
	{
		log.info("Repository namespace={}, name={} added.", namespace, name);

		Repository repository = getRepository(namespace, name, object);
		repositoryRegistry.add(repository);
	}

	@Override
	public void repositoryModified(String namespace, String name, KObject object)
	{
		Repository repository = getRepository(namespace, name, object);
		repositoryRegistry.add(repository);
	}

	@Override
	public void repositoryDeleted(String namespace, String name, KObject object)
	{
		Repository repository = getRepository(namespace, name, object);
		repositoryRegistry.remove(repository);
	}

	private Repository getRepository(String namespace, String name, KObject object)
	{
		KObject spec = object.get("spec");
		String uri = spec.getString("url");
		String branch = spec.getString("branch", "master");
		return new Repository(namespace, name, uri, branch);
	}
}

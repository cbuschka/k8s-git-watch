package com.github.cbuschka.gitwatch.controller;

import java.util.HashSet;
import java.util.Set;

public class RepositoryRegistry
{
	private final Set<Repository> repositories = new HashSet<>();

	public synchronized void add(Repository repository)
	{
		repositories.add(repository);
	}

	public synchronized Set<Repository> snapshot()
	{
		return new HashSet<>(this.repositories);
	}

	public synchronized void remove(Repository repository)
	{
		this.repositories.remove(repository);
	}
}

package com.github.cbuschka.git_watch.controller;

import java.util.HashSet;
import java.util.Set;

public class RepositoryRegistry
{
	private Set<Repository> repositories = new HashSet<>();

	{
		repositories.add(new Repository("testrepo", "https://github.com/cbuschka/testrepo.git", "master"));
	}

	public synchronized void add(Repository repository)
	{
		repositories.add(repository);
	}

	public synchronized Set<Repository> snapshot()
	{
		return new HashSet<>(this.repositories);
	}


}

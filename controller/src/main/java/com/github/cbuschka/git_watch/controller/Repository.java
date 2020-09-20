package com.github.cbuschka.git_watch.controller;

import java.util.Objects;

public class Repository
{
	public final String name;

	public final String remote;

	public final String branch;

	public Repository(String name, String remote, String branch)
	{
		this.name = name;
		this.remote = remote;
		this.branch = branch;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Repository that = (Repository) o;
		return remote.equals(that.remote);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(remote);
	}
}

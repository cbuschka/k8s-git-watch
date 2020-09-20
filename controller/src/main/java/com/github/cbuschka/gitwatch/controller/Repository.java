package com.github.cbuschka.gitwatch.controller;

import java.util.Objects;

public class Repository
{
	public final String name;
	public final String uri;
	public final String branch;
	public final String namespace;

	public Repository(String namespace, String name, String uri, String branch)
	{
		this.namespace = namespace;
		this.name = name;
		this.uri = uri;
		this.branch = branch;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Repository that = (Repository) o;
		return (namespace + ":" + name).equals(that.namespace + ":" + that.name);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.namespace + ":" + this.name);
	}
}

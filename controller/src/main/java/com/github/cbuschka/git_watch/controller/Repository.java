package com.github.cbuschka.git_watch.controller;

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
}

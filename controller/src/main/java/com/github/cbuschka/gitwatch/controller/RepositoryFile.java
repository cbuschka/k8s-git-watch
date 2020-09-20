package com.github.cbuschka.gitwatch.controller;

import java.io.File;
import java.io.IOException;

public class RepositoryFile
{
	public final Repository repository;
	public final File ymlFile;
	public final File repoDir;
	private final String relativePath;

	public RepositoryFile(Repository repository, File ymlFile, File repoDir) throws IOException
	{
		this.repository = repository;
		this.ymlFile = ymlFile;
		this.repoDir = repoDir;

		this.relativePath = repository.uri + "/" + ymlFile.getCanonicalPath().substring(repoDir.getCanonicalPath().length() + 1);
	}

	public String toString()
	{
		return relativePath;
	}
}

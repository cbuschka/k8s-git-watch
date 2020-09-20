package com.github.cbuschka.gitwatch.controller;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class YmlFileCollector
{
	public Set<File> collectYmlFiles(File repoDir)
	{
		Set<File> ymlFiles = new HashSet<>();
		collectYmlFilesInto(repoDir, repoDir, ymlFiles);
		return ymlFiles;
	}

	private void collectYmlFilesInto(File dir, File repoDir, Set<File> ymlFiles)
	{

		File[] files = dir.listFiles();
		if (files != null)
		{
			for (File file : files)
			{
				if (file.isDirectory() && !file.getName().equals(".git"))
				{
					collectYmlFilesInto(file, repoDir, ymlFiles);
				}
				else if (file.isFile() && (file.getName().endsWith(".yml") || file.getName().endsWith(".yaml")))
				{
					ymlFiles.add(file);
				}
			}
		}
	}
}

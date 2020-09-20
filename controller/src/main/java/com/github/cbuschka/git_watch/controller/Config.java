package com.github.cbuschka.git_watch.controller;

import java.util.List;

public class Config
{
	public Handler handler;

	public Filter filter;

	public static Config defaultConfig()
	{
		Config d = new Config();
		d.handler = null;
		d.filter = new Filter();
		return d;
	}

	public static class Handler
	{
	}

	public static class Filter
	{
		public List<String> namespaces;

		public List<String> kinds;

		public List<String> names;
	}
}

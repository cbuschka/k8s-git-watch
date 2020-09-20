package com.github.cbuschka.mykubewatch;

import java.util.List;

public class MyKubeWatchConfig
{
	public Handler handler;

	public Filter filter;

	public static MyKubeWatchConfig defaultConfig()
	{
		MyKubeWatchConfig d = new MyKubeWatchConfig();
		d.handler = null;
		d.filter = new Filter();
		return d;
	}

	public static class Handler
	{

		public Mattermost mattermost;
	}

	public static class Mattermost
	{
		public String url;
		public String username;
		public String channel;
	}

	public static class Filter
	{
		public List<String> namespaces;

		public List<String> kinds;

		public List<String> names;
	}
}

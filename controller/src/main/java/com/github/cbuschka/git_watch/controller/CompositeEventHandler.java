package com.github.cbuschka.git_watch.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompositeEventHandler implements EventHandler
{
	private List<EventHandler> delegates = new ArrayList<>();

	public void addDelegate(EventHandler delegate)
	{
		this.delegates.add(delegate);
	}

	@Override
	public void handle(String type, String kind, String namespace, String name, Map<String, Object> data)
	{
		for (EventHandler delegate : delegates)
		{
			delegate.handle(type, kind, namespace, name, data);
		}
	}
}

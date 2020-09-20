package com.github.cbuschka.mykubewatch;

import java.util.Map;

public class ConsoleEventHandler implements EventHandler
{
	@Override
	public void handle(String type, String kind, String namespace, String name, Map<String, Object> data)
	{
		String message = String.format("%s : %s named %s", type, kind, name);
		System.err.println(message);
	}
}

package com.github.cbuschka.mykubewatch;

import java.util.Map;

public interface EventHandler
{
	void handle(String type, String kind, String namespace, String name, Map<String, Object> data);
}

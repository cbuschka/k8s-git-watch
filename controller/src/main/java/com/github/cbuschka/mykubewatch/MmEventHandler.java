package com.github.cbuschka.mykubewatch;

import java.util.Map;

public class MmEventHandler implements EventHandler
{
	private MmClient mmClient;

	public MmEventHandler(MmClient mmClient)
	{
		this.mmClient = mmClient;
	}

	@Override
	public void handle(String type, String kind, String namespace, String name, Map<String, Object> data)
	{
		String message = String.format("%s : %s named %s", type, kind, name);
		this.mmClient.post(message);
	}
}

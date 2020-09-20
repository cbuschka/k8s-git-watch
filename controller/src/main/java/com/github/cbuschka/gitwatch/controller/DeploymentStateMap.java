package com.github.cbuschka.gitwatch.controller;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class DeploymentStateMap
{
	private Map<String, BigInteger> stateMap = new HashMap<>();

	public synchronized void update(String key, BigInteger hashCode)
	{
		this.stateMap.put(key, hashCode);
	}

	public synchronized boolean isUptodate(String key, BigInteger hashCode)
	{
		BigInteger currentHashCode = this.stateMap.get(key);
		return currentHashCode != null && hashCode.equals(currentHashCode);
	}
}

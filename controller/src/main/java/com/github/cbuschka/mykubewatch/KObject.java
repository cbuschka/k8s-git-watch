package com.github.cbuschka.mykubewatch;

import java.util.Map;

public class KObject
{
	private Object underlying;

	public static KObject wrap(Object underlying)
	{
		return new KObject(underlying);
	}

	public KObject(Object underlying)
	{
		this.underlying = underlying;
	}

	public String getString(String key)
	{
		return (String) asMap(this.underlying).get(key);
	}

	public KObject get(String key)
	{
		Object value = asMap(this.underlying).get(key);
		return new KObject(value);
	}

	public Map<String, Object> asMap()
	{
		return asMap(this.underlying);
	}

	private Map<String, Object> asMap(Object o)
	{
		return (Map<String, Object>) o;
	}
}

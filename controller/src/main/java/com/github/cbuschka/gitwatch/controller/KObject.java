package com.github.cbuschka.gitwatch.controller;

import java.util.Map;

public class KObject
{
	private final Object underlying;

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
		return getString(key, null);
	}

	public String getString(String key, String defaultValue)
	{
		if (this.underlying == null)
		{
			return defaultValue;
		}

		Map<String, Object> map = asMap(this.underlying);
		String value = (String) map.get(key);
		if (value != null)
		{
			return value;
		}
		return defaultValue;
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

	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap(Object o)
	{
		return (Map<String, Object>) o;
	}
}

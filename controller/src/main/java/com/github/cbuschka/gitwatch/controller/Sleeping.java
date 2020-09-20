package com.github.cbuschka.gitwatch.controller;

public class Sleeping
{
	public static void sleepQuietly(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

}

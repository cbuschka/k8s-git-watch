package com.github.cbuschka.gitwatch.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws InterruptedException
	{
		System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
		System.setProperty("java.net.preferIPv4Stack", "true");

		while (!Thread.interrupted())
		{
			try
			{
				Controller controller = new Controller();
				controller.run();
				return;
			}
			catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
			catch (Exception ex)
			{
				log.error("Failure.", ex);
				Sleeping.sleepQuietly(1000);
			}
		}
	}
}
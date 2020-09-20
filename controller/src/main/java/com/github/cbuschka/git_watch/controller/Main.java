package com.github.cbuschka.git_watch.controller;


import io.kubernetes.client.openapi.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main
{
	private static Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws IOException, ApiException, InterruptedException
	{
		System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
		System.setProperty("java.net.preferIPv4Stack", "true");

		while (true)
		{
			try
			{
				Controller controller = new Controller();
				controller.run();
				return;
			}
			catch (Exception ex)
			{
				log.error("Failure.", ex);
				Thread.sleep(1000);
			}
		}
	}
}
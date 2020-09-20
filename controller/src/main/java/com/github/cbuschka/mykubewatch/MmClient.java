package com.github.cbuschka.mykubewatch;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MmClient
{
	private final String url;
	private final String username;
	private final String channel;

	public MmClient(String url, String channel, String username)
	{
		this.url = url;
		this.username = username;
		this.channel = channel;
	}

	public void post(String message)
	{
		HttpURLConnection conn = null;
		try
		{
			conn = (HttpURLConnection) (new URL(this.url).openConnection());
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setInstanceFollowRedirects(false);

			Gson gson = new Gson();
			HashMap<String, Object> data = new HashMap<>();
			data.put("text", message);
			String json = gson.toJson(data);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
			wr.write(json);
			wr.flush();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = rd.readLine()) != null)
			{
				System.out.println(line);
			}
			conn.disconnect();
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
			}
		}
	}
}

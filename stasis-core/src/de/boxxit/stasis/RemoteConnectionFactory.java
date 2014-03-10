package de.boxxit.stasis;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Christian Fruth
 */
public class RemoteConnectionFactory
{
	private URL url;

	public RemoteConnectionFactory()
	{
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public void setUrl(String url)
	{
		try
		{
			this.url = new URL(url);
		}
		catch (MalformedURLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public RemoteConnection createConnection()
	{
		String protocol = url.getProtocol();

		if ("http".equals(protocol))
		{
			return new HttpRemoteConnection(url);
		}

		if ("https".equals(protocol))
		{
			return new HttpRemoteConnection(url);
		}

		return null;
	}

	public static RemoteConnection createConnection(String url)
	{
		RemoteConnectionFactory remoteConnectionFactory = new RemoteConnectionFactory();
		remoteConnectionFactory.setUrl(url);
		return remoteConnectionFactory.createConnection();
	}

	public static RemoteConnection createConnection(URL url)
	{
		RemoteConnectionFactory remoteConnectionFactory = new RemoteConnectionFactory();
		remoteConnectionFactory.setUrl(url);
		return remoteConnectionFactory.createConnection();
	}
}

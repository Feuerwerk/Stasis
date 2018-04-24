package de.boxxit.stasis;

import java.net.MalformedURLException;
import java.net.URL;
import com.esotericsoftware.kryo.pool.KryoPool;

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

	public RemoteConnection createConnection(KryoPool.KryoFactory kryoFactory)
	{
		String protocol = url.getProtocol();

		if ("http".equals(protocol))
		{
			return new HttpRemoteConnection(url, kryoFactory);
		}

		if ("https".equals(protocol))
		{
			return new HttpRemoteConnection(url, kryoFactory);
		}

		return null;
	}

	public static RemoteConnection createConnection(String url, KryoPool.KryoFactory kryoFactory)
	{
		RemoteConnectionFactory remoteConnectionFactory = new RemoteConnectionFactory();
		remoteConnectionFactory.setUrl(url);
		return remoteConnectionFactory.createConnection(kryoFactory);
	}

	public static RemoteConnection createConnection(URL url, KryoPool.KryoFactory kryoFactory)
	{
		RemoteConnectionFactory remoteConnectionFactory = new RemoteConnectionFactory();
		remoteConnectionFactory.setUrl(url);
		return remoteConnectionFactory.createConnection(kryoFactory);
	}
}

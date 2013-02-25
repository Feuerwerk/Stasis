package de.boxxit.statis;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Christian Fruth
 */
public abstract class Connection
{
	protected String userName;
	protected String password;
	protected ConnectionState state;

	public static Connection createConnection(String url)
	{
		try
		{
			return createConnection(new URL(url));
		}
		catch (MalformedURLException ex)
		{
			return null;
		}
	}

	public static Connection createConnection(URL url)
	{
		String protocol = url.getProtocol();

		if ("http".equals(protocol))
		{
			return new HttpConnection(url);
		}

		if ("https".equals(protocol))
		{
			return new HttpConnection(url);
		}

		return null;
	}

	public abstract <T> T invoke(Class<T> returnType, String serviceName, Object... args) throws IOException;

	public void setCredentials(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
		this.state = ConnectionState.Connected;
	}

	public ConnectionState getState()
	{
		return state;
	}

	protected Connection()
	{
	}
}

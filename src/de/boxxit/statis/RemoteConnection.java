package de.boxxit.statis;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Christian Fruth
 */
public abstract class RemoteConnection
{
	protected String userName;
	protected String password;
	protected ConnectionState state;

	public static RemoteConnection createConnection(String url)
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

	public static RemoteConnection createConnection(URL url)
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

	public abstract <T> T invoke(Class<T> returnType, String serviceName, Object... args) throws IOException;

	public void setCredentials(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
		this.state = ConnectionState.Connected;
	}

	public void login(String userName, String password) throws IOException, AuthenticationException
	{
		setCredentials(userName, password);
		login();
	}

	public abstract void login() throws IOException, AuthenticationException;

	public ConnectionState getState()
	{
		return state;
	}

	protected RemoteConnection()
	{
	}
}

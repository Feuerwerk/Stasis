package de.boxxit.statis;

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

	public abstract <T> void call(CallHandler<T> handler, String name, Object... args);
    public abstract <T> T callSync(String name, Object... args) throws Exception;

	public void setCredentials(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
		this.state = ConnectionState.Connected;
	}

	public void login(CallHandler<Void> handler, String userName, String password)
	{
		setCredentials(userName, password);
		login(handler);
	}

	public abstract void login(CallHandler<Void> handler);

	public ConnectionState getState()
	{
		return state;
	}

	protected RemoteConnection()
	{
	}
}

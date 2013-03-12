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
	protected Synchronizer synchronizer;

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

	public Synchronizer getSynchronizer()
	{
		return synchronizer;
	}

	public void setSynchronizer(Synchronizer synchronizer)
	{
		this.synchronizer = synchronizer;
	}

	public abstract <T> void callAsync(CallHandler<T> handler, String name, Object... args);

	public abstract <T> T callSync(String name, Object... args) throws Exception;

	public void setCredentials(String userName, String password)
	{
		this.userName = userName;
		this.password = password;

		if (state == ConnectionState.Authenticated)
		{
			this.state = ConnectionState.Connected;
		}
	}

	public abstract void login(CallHandler<Void> handler, String userName, String password);

	public ConnectionState getState()
	{
		return state;
	}

	protected RemoteConnection()
	{
	}
}

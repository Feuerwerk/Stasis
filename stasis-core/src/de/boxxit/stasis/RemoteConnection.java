package de.boxxit.stasis;

import java.net.MalformedURLException;
import java.net.URL;
import com.esotericsoftware.kryo.Serializer;

/**
 * User: Christian Fruth
 */
public abstract class RemoteConnection
{
	protected String userName;
	protected String password;
	protected int clientVersion;
	protected ConnectionState state;
	protected Synchronizer synchronizer;
	protected HandshakeHandler handshakeHandler;

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

	public HandshakeHandler getHandshakeHandler()
	{
		return handshakeHandler;
	}

	public void setHandshakeHandler(HandshakeHandler handshakeHandler)
	{
		this.handshakeHandler = handshakeHandler;
	}

	public int getClientVersion()
	{
		return clientVersion;
	}

	public String getPassword()
	{
		return password;
	}

	public String getUserName()
	{
		return userName;
	}

	public abstract void setDefaultSerializer(Class<? extends Serializer> defaultSerializer);

	public abstract <T> void callAsync(CallHandler<T> handler, String name, Object... args);

	public abstract <T> T callSync(String name, Object... args) throws Exception;

	public void setCredentials(String userName, String password, int clientVersion)
	{
		this.userName = userName;
		this.password = password;
		this.clientVersion = clientVersion;

		if (state == ConnectionState.Authenticated)
		{
			this.state = ConnectionState.Connected;
		}
	}

	public abstract void login(CallHandler<Void> handler, String userName, String password, int clientVersion);

	public ConnectionState getState()
	{
		return state;
	}

	public abstract <T> void register(Class<T> type, int id);

	public abstract <T> void register(Class<T> type, Serializer<T> serializer);

	public abstract <T> void register(Class<T> type, Serializer<T> serializer, int id);

	protected RemoteConnection()
	{
	}
}

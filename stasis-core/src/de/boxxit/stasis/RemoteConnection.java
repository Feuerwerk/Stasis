package de.boxxit.stasis;

import com.esotericsoftware.kryo.Serializer;

/**
 * User: Christian Fruth
 */
public interface RemoteConnection
{
	/*
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
	*/

	public void setSynchronizer(Synchronizer synchronizer);
	public void setHandshakeHandler(HandshakeHandler handshakeHandler);
	public void setDefaultSerializer(Class<? extends Serializer> defaultSerializer);
	public void setCredentials(String userName, String password, int clientVersion);

	public ConnectionState getState();
	public int getClientVersion();
	public String getPassword();
	public String getUserName();

	public <T> void register(Class<T> type, int id);
	public <T> void register(Class<T> type, Serializer<T> serializer);
	public <T> void register(Class<T> type, Serializer<T> serializer, int id);

	public <T> void callAsync(CallHandler<T> handler, String name, Object... args);
	public <T> T callSync(String name, Object... args) throws Exception;
	public void login(CallHandler<Void> handler, String userName, String password, int clientVersion);
}

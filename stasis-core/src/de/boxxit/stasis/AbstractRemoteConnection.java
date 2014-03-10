package de.boxxit.stasis;

import de.boxxit.stasis.ConnectionState;
import de.boxxit.stasis.HandshakeHandler;
import de.boxxit.stasis.RemoteConnection;
import de.boxxit.stasis.Synchronizer;

/**
 * User: Christian Fruth
 */
public abstract class AbstractRemoteConnection implements RemoteConnection
{
	protected String userName;
	protected String password;
	protected int clientVersion;
	protected ConnectionState state;
	protected Synchronizer synchronizer;
	protected HandshakeHandler handshakeHandler;

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

	public ConnectionState getState()
	{
		return state;
	}
}

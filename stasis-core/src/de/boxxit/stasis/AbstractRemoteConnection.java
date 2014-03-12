package de.boxxit.stasis;

import java.util.Map;

/**
 * User: Christian Fruth
 */
public abstract class AbstractRemoteConnection implements RemoteConnection
{
	protected String userName;
	protected String password;
	protected Map<String, Object> parameters;
	protected ConnectionState state;
	protected Synchronizer synchronizer;
	protected HandshakeHandler handshakeHandler;

	public Synchronizer getSynchronizer()
	{
		return synchronizer;
	}

	@Override
	public void setSynchronizer(Synchronizer synchronizer)
	{
		this.synchronizer = synchronizer;
	}

	public HandshakeHandler getHandshakeHandler()
	{
		return handshakeHandler;
	}

	@Override
	public void setHandshakeHandler(HandshakeHandler handshakeHandler)
	{
		this.handshakeHandler = handshakeHandler;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public String getUserName()
	{
		return userName;
	}

	@Override
	public Map<String, Object> getParameters()
	{
		return parameters;
	}

	@Override
	public void setCredentials(String userName, String password, Map<String, Object> parameters)
	{
		this.userName = userName;
		this.password = password;
		this.parameters = parameters;

		if (state == ConnectionState.Authenticated)
		{
			this.state = ConnectionState.Connected;
		}
	}

	@Override
	public ConnectionState getState()
	{
		return state;
	}

	protected AbstractRemoteConnection()
	{
	}
}

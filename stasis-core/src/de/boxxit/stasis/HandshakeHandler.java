package de.boxxit.stasis;

import java.net.HttpURLConnection;

/**
 * User: Christian Fruth
 */
public interface HandshakeHandler
{
	public boolean handleResponse(HttpURLConnection urlConnection, RemoteConnection remoteConnection, int tryCount);
}

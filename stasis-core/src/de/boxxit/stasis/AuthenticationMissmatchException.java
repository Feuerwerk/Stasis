package de.boxxit.stasis;

import com.esotericsoftware.kryo.DefaultSerializer;
import de.boxxit.stasis.serializer.AuthenticationMissmatchExceptionSerializer;

/**
 * User: Christian Fruth
 */
@DefaultSerializer(AuthenticationMissmatchExceptionSerializer.class)
public class AuthenticationMissmatchException extends Exception
{
	private boolean authenticated;

	public AuthenticationMissmatchException(boolean authenticated)
	{
		this.authenticated = authenticated;
	}

	public boolean isAuthenticated()
	{
		return authenticated;
	}
}

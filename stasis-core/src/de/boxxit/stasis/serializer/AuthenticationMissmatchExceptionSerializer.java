package de.boxxit.stasis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.AuthenticationMissmatchException;

/**
 * User: Christian Fruth
 */
public class AuthenticationMissmatchExceptionSerializer extends Serializer<AuthenticationMissmatchException>
{
	public AuthenticationMissmatchExceptionSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, AuthenticationMissmatchException object)
	{
		output.writeBoolean(object.isAuthenticated());
	}

	@Override
	public AuthenticationMissmatchException read(Kryo kryo, Input input, Class<AuthenticationMissmatchException> type)
	{
		boolean authenticated = input.readBoolean();
		return new AuthenticationMissmatchException(authenticated);
	}
}

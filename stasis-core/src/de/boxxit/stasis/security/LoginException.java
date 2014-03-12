package de.boxxit.stasis.security;

/**
 * User: Christian Fruth
 */
public class LoginException extends Exception
{
	public LoginException()
	{
	}

	public LoginException(String message)
	{
		super(message);
	}

	public LoginException(String message, Throwable ex)
	{
		super(message, ex);
	}

	public LoginException(Throwable ex)
	{
		super(ex);
	}
}

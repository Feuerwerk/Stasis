package de.boxxit.stasis;

import java.util.Map;

/**
 * User: Christian Fruth
 */
public class AuthenticationException extends StasisException
{
	private Map<String, Object> userInfo;

	public AuthenticationException(String id, String message, Map<String, Object> userInfo)
	{
		super(id, message);
		this.userInfo = userInfo;
	}

	public Map<String, Object> getUserInfo()
	{
		return userInfo;
	}
}

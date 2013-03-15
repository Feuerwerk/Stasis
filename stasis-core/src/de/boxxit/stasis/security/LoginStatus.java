package de.boxxit.stasis.security;

/**
 * User: Christian Fruth
 */
public class LoginStatus
{
	private boolean authenticated;
	private String userName;

	public LoginStatus(boolean authenticated, String userName)
	{
		this.authenticated = authenticated;
		this.userName = userName;
	}

	public boolean isAuthenticated()
	{
		return authenticated;
	}

	public String getUserName()
	{
		return userName;
	}
}

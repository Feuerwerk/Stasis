package de.boxxit.stasis.security;

/**
 * User: Christian Fruth
 */
public interface LoginService
{
	public LoginStatus getStatus() throws LoginException;

	public LoginStatus login(String username, String password) throws LoginException;

	public void logout() throws LoginException;
}

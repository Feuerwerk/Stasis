package de.boxxit.statis.security;

/**
 * User: Christian Fruth
 */
public interface LoginService
{
	public LoginStatus getStatus();
	public LoginStatus login(String username, String password);
}

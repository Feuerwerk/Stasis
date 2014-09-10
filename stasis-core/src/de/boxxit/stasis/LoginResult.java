package de.boxxit.stasis;

import java.util.Map;

/**
 * User: Christian Fruth
 */
public class LoginResult
{
	private AuthenticationResult authenticationResult;
	private Map<String, Object> loginResponse;

	public LoginResult()
	{
	}

	public LoginResult(AuthenticationResult authenticationResult, Map<String, Object> loginResponse)
	{
		this.authenticationResult = authenticationResult;
		this.loginResponse = loginResponse;
	}

	public AuthenticationResult getAuthenticationResult()
	{
		return authenticationResult;
	}

	public void setAuthenticationResult(AuthenticationResult authenticationResult)
	{
		this.authenticationResult = authenticationResult;
	}

	public Map<String, Object> getLoginResponse()
	{
		return loginResponse;
	}

	public void setLoginResponse(Map<String, Object> loginResponse)
	{
		this.loginResponse = loginResponse;
	}
}

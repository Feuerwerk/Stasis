package de.boxxit.stasis.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: Christian Fruth
 */
@Service("javaeeLoginService")
public class JavaEELoginService implements LoginService
{
	@Autowired
	private HttpServletRequest request;

	public JavaEELoginService()
	{
	}

	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	public LoginStatus getStatus() throws LoginException
	{
		if (request.getRemoteUser() != null)
		{
			return new LoginStatus(true, request.getRemoteUser());
		}
		else
		{
			return new LoginStatus(false, null);
		}
	}

	@Override
	public LoginStatus login(String username, String password) throws LoginException
	{
		try
		{
			if (request.getRemoteUser() == null)
			{
				request.login(username, password);
			}
			return new LoginStatus(true, request.getRemoteUser());
		}
		catch (ServletException ex)
		{
			throw new LoginException("login error", ex);
		}
	}

	@Override
	public void logout() throws LoginException
	{
		try
		{
			request.logout();
		}
		catch (ServletException ex)
		{
			throw new LoginException("logout error", ex);
		}
	}
}



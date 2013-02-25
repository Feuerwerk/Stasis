package de.boxxit.statis.security;

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

	public LoginStatus getStatus()
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
	public LoginStatus login(String username, String password)
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
			ex.printStackTrace();
			return new LoginStatus(false, null);
		}
	}
}



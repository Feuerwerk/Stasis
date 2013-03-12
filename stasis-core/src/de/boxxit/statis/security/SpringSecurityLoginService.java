package de.boxxit.statis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * User: Christian Fruth
 */
@Service("springLoginService")
public class SpringSecurityLoginService implements LoginService
{
	@Autowired(required = false)
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	public SpringSecurityLoginService()
	{
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager)
	{
		this.authenticationManager = authenticationManager;
	}

	@Override
	public LoginStatus getStatus()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if ((authentication != null) && !authentication.getName().equals("anonymousUser") && authentication.isAuthenticated())
		{
			return new LoginStatus(true, authentication.getName());
		}
		else
		{
			return new LoginStatus(false, null);
		}
	}

	@Override
	public LoginStatus login(String username, String password)
	{
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

		try
		{
			Authentication authentication = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			return new LoginStatus(authentication.isAuthenticated(), authentication.getName());
		}
		catch (BadCredentialsException e)
		{
			return new LoginStatus(false, null);
		}
	}
}

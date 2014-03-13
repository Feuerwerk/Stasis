package de.boxxit.stasis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
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
	public LoginStatus getStatus() throws LoginException
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
	public LoginStatus login(String username, String password) throws LoginException
	{
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

		try
		{
			Authentication authentication = authenticationManager.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			return new LoginStatus(authentication.isAuthenticated(), authentication.getName());
		}
		catch (BadCredentialsException | DisabledException ex)
		{
			return new LoginStatus(false, null);
		}
		catch (AuthenticationException ex)
		{
			throw new LoginException(ex);
		}
	}

	@Override
	public void logout() throws LoginException
	{
		SecurityContext securityContext = SecurityContextHolder.getContext();

		if (securityContext != null)
		{
			securityContext.setAuthentication(null);
		}

		SecurityContextHolder.clearContext();
	}
}

package de.boxxit.stasis.spring;

import java.util.Map;
import de.boxxit.stasis.security.LoginException;
import de.boxxit.stasis.security.LoginStatus;

/**
 * User: Christian Fruth
 */
public interface LoginValidator
{
	public boolean preAuthenticate(Map<String, Object> request, Map<String, Object> response) throws LoginException;
	public boolean postAuthenticate(LoginStatus loginStatus, Map<String, Object> request, Map<String, Object> response) throws LoginException;
}

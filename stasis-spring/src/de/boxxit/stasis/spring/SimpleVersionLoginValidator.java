package de.boxxit.stasis.spring;

import java.util.Map;
import de.boxxit.stasis.StasisConstants;
import de.boxxit.stasis.security.LoginException;
import de.boxxit.stasis.security.LoginStatus;

/**
 * User: Christian Fruth
 */
public class SimpleVersionLoginValidator implements LoginValidator
{
	private int serverVersion;
	private String vmmRedirectPath;
	private String vmmSerializerHint;

	public SimpleVersionLoginValidator()
	{
	}

	public void setServerVersion(int serverVersion)
	{
		this.serverVersion = serverVersion;
	}

	public void setVmmRedirectPath(String vmmRedirectPath)
	{
		this.vmmRedirectPath = vmmRedirectPath;
	}

	public void setVmmSerializerHint(String vmmSerializerHint)
	{
		this.vmmSerializerHint = vmmSerializerHint;
	}

	@Override
	public boolean preAuthenticate(Map<String, Object> request, Map<String, Object> response) throws LoginException
	{
		Number clientVersion = (Number)request.get(StasisConstants.VERSION_NUMBER_KEY);

		if ((clientVersion.intValue() != serverVersion) && (clientVersion.intValue() != 0) && (serverVersion != 0))
		{
			response.put(StasisConstants.VERSION_MISSMATCH_KEY, true);

			if (vmmRedirectPath != null)
			{
				response.put(StasisConstants.REDIRECT_PATH_KEY, vmmRedirectPath);

				if (vmmSerializerHint != null)
				{
					response.put(StasisConstants.SERIALIZER_HINT_KEY, vmmSerializerHint);
				}
			}

			return false;
		}

		return true;
	}

	@Override
	public boolean postAuthenticate(LoginStatus loginStatus, Map<String, Object> request, Map<String, Object> response) throws LoginException
	{
		return true;
	}
}

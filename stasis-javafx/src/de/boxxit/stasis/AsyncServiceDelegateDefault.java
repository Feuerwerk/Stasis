package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public interface AsyncServiceDelegateDefault extends AsyncServiceDelegate
{
	public default void serviceCallWillBegin()
	{

	}

	public default void serviceCallDidFinish()
	{

	}

	public default void serviceCallFailed(Exception ex)
	{

	}
}

package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public interface AsyncServiceDelegateDefault extends AsyncServiceDelegate
{
	default void serviceCallWillBegin()
	{

	}

	default void serviceCallDidFinish()
	{

	}

	default void serviceCallFailed(Exception ex)
	{

	}
}

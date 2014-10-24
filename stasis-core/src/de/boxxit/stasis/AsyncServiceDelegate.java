package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public interface AsyncServiceDelegate
{
	public void serviceCallWillBegin();
	public void serviceCallWillFinish();
	public void serviceCallDidFinish();
	public void serviceCallFailed(Exception ex);
}

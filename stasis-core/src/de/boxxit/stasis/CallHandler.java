package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public interface CallHandler<T>
{
	public void callWillBegin();

	public void callSucceeded(T value);

	public void callFailed(Exception ex);

	public void callWillFinish();

	public void callDidFinish();
}

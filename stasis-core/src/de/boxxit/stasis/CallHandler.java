package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public interface CallHandler<T>
{
	public void succeeded(T value);

	public void failed(Exception ex);
}

package de.boxxit.statis;

/**
 * User: Christian Fruth
 */
public interface CallHandler<T>
{
	public void succeeded(T value);

	public void failed(Exception ex);
}

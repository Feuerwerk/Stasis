package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public interface ResultHandler<T>
{
	public void handle(T value);
}

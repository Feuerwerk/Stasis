package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public interface ErrorHandler
{
	public void failed(Exception ex, ErrorChain errorChain);
}

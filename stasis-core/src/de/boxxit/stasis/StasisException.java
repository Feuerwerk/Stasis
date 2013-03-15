package de.boxxit.stasis;

/**
 * User: Christian Fruth
 */
public class StasisException extends RuntimeException
{
	private String id;

	public StasisException(String id, String message)
	{
		super(message);
		this.id = id;
	}

	public String getId()
	{
		return id;
	}
}

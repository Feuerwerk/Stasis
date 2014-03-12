package de.boxxit.stasis;

import com.esotericsoftware.kryo.DefaultSerializer;
import de.boxxit.stasis.serializer.MessageExceptionSerializer;

/**
 * User: Christian Fruth
 */
@DefaultSerializer(MessageExceptionSerializer.class)
public class MessageException extends Exception
{
	private String id;

	public MessageException(String id, String message)
	{
		super(message);
		this.id = id;
	}

	public MessageException(Throwable ex)
	{
		super(ex.getMessage());
		this.id = ex.getClass().getName();
	}

	public String getId()
	{
		return id;
	}
}

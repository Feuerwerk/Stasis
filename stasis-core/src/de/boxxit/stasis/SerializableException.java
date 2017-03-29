package de.boxxit.stasis;

import com.esotericsoftware.kryo.DefaultSerializer;
import de.boxxit.stasis.serializer.SerializableExceptionSerializer;

/**
 * User: Christian Fruth
 */
@DefaultSerializer(SerializableExceptionSerializer.class)
public class SerializableException extends RuntimeException
{
	private String type;

	public SerializableException(String type, String message)
	{
		super(message);
		this.type = type;
	}

	public SerializableException(Throwable ex)
	{
		super(ex.getMessage());
		this.type = ex.getClass().getName();
	}

	public String getType()
	{
		return type;
	}
}

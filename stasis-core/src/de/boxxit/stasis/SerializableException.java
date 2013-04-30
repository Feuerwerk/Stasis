package de.boxxit.stasis;

import com.esotericsoftware.kryo.DefaultSerializer;
import de.boxxit.stasis.serializer.SerializableExceptionSerializer;

/**
 * User: Christian Fruth
 */
@DefaultSerializer(SerializableExceptionSerializer.class)
public class SerializableException extends RuntimeException
{
	private String id;

	public SerializableException(String id, String message)
	{
		super(message);
		this.id = id;
	}

	public SerializableException(Throwable ex)
	{
		super(ex.getMessage());
		this.id = ex.getClass().getName();
	}

	public String getId()
	{
		return id;
	}
}
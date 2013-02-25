package de.boxxit.statis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.statis.SerializableException;

/**
 * User: Christian Fruth
 */
public class SerializableExceptionSerializer extends Serializer<SerializableException>
{
	public SerializableExceptionSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, SerializableException object)
	{
		kryo.writeObjectOrNull(output, object.getId(), String.class);
		kryo.writeObjectOrNull(output, object.getMessage(), String.class);
	}

	@Override
	public SerializableException read(Kryo kryo, Input input, Class<SerializableException> type)
	{
		String id = kryo.readObjectOrNull(input, String.class);
		String message = kryo.readObjectOrNull(input, String.class);
		return new SerializableException(id, message);
	}
}

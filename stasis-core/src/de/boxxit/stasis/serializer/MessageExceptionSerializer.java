package de.boxxit.stasis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.MessageException;

/**
 * User: Christian Fruth
 */
public class MessageExceptionSerializer extends Serializer<MessageException>
{
	public MessageExceptionSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, MessageException object)
	{
		kryo.writeObjectOrNull(output, object.getId(), String.class);
		kryo.writeObjectOrNull(output, object.getMessage(), String.class);
	}

	@Override
	public MessageException read(Kryo kryo, Input input, Class<MessageException> type)
	{
		String id = kryo.readObjectOrNull(input, String.class);
		String message = kryo.readObjectOrNull(input, String.class);
		return new MessageException(id, message);
	}
}

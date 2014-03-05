package de.boxxit.stasis.serializer;

import java.util.UUID;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * User: Christian Fruth
 */
public class UUIDSerializer extends Serializer<UUID>
{
	public UUIDSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, UUID object)
	{
		output.writeLong(object.getMostSignificantBits());
		output.writeLong(object.getLeastSignificantBits());
	}

	@Override
	public UUID read(Kryo kryo, Input input, Class<UUID> type)
	{
		long mostSignificantBits = input.readLong();
		long leastSignificantBits = input.readLong();
		return new UUID(mostSignificantBits, leastSignificantBits);
	}
}

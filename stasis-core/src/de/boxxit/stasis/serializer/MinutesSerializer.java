package de.boxxit.stasis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.joda.time.Minutes;

/**
 * User: Christian Fruth
 */
public class MinutesSerializer extends Serializer<Minutes>
{
	public MinutesSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, Minutes minutes)
	{
		output.writeInt(minutes.getMinutes(), true);
	}

	@Override
	public Minutes read(Kryo kryo, Input input, Class<Minutes> type)
	{
		return Minutes.minutes(input.readInt(true));
	}
}

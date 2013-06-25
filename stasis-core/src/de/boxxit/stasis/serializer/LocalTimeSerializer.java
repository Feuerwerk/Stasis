package de.boxxit.stasis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.joda.time.LocalTime;

/**
 * User: Christian Fruth
 */
public class LocalTimeSerializer extends Serializer<LocalTime>
{
	public LocalTimeSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, LocalTime time)
	{
		long millisOfDay = time.getMillisOfDay();
		output.writeLong(millisOfDay, true);
	}

	@Override
	public LocalTime read(Kryo kryo, Input input, Class<LocalTime> type)
	{
		long millisOfDay = input.readLong(true);
		return LocalTime.fromMillisOfDay(millisOfDay);
	}
}

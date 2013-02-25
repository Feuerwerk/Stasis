package de.boxxit.statis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.joda.time.LocalDateTime;

/**
 * User: Christian Fruth
 */
public class LocalDateTimeSerializer extends Serializer<LocalDateTime>
{
	public LocalDateTimeSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, LocalDateTime date)
	{
		output.writeLong(date.toDateTime().getMillis(), true);
	}

	@Override
	public LocalDateTime read(Kryo kryo, Input input, Class<LocalDateTime> type)
	{
		long millis = input.readLong(true);
		return new LocalDateTime(millis);
	}
}

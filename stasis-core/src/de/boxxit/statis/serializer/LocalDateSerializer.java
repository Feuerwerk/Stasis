package de.boxxit.statis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

/**
 * User: Christian Fruth
 */
public class LocalDateSerializer extends Serializer<LocalDate>
{
	public LocalDateSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, LocalDate date)
	{
		long millis = date.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis();
		output.writeLong(millis, true);
	}

	@Override
	public LocalDate read(Kryo kryo, Input input, Class<LocalDate> type)
	{
		long millis = input.readLong(true);
		return new LocalDate(millis, DateTimeZone.UTC);
	}
}

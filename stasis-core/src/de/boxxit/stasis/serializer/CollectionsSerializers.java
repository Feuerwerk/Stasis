package de.boxxit.stasis.serializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * User: Christian Fruth
 */
public class CollectionsSerializers
{
	public static class UnmodifiableListSerializer extends Serializer<List>
	{
		{
			setImmutable(true);
		}

		@Override
		public void write(Kryo kryo, Output output, List list)
		{
			output.writeBoolean(list instanceof RandomAccess);
			int length = list.size();
			output.writeInt(length, true);

			for (Object element : list)
			{
				kryo.writeClassAndObject(output, element);
			}
		}

		@Override
		public List read(Kryo kryo, Input input, Class type)
		{
			boolean randomAccess = input.readBoolean();
			int length = input.readInt(true);
			List<Object> list = randomAccess ? new ArrayList<Object>(length) : new LinkedList<Object>();

			kryo.reference(list);

			for (int i = 0; i < length; i++)
			{
				list.add(kryo.readClassAndObject(input));
			}

			return Collections.unmodifiableList(list);
		}
	}
}

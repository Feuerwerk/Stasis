package de.boxxit.stasis.serializer;

import java.util.ArrayList;
import java.util.Collection;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;

/**
 * User: Christian Fruth
 */
public class ArraysListSerializer extends CollectionSerializer
{
	public ArraysListSerializer()
	{
	}

	protected Collection create (Kryo kryo, Input input, Class<Collection> type)
	{
		return new ArrayList();
	}
}

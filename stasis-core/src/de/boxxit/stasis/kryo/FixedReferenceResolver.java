package de.boxxit.stasis.kryo;

import java.util.ArrayList;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ReferenceResolver;
import com.esotericsoftware.kryo.util.Util;

/**
 * User: Christian Fruth
 */
public class FixedReferenceResolver implements ReferenceResolver
{
	@SuppressWarnings("unchecked")
	private final IdentityObjectIntMap writtenObjects = new IdentityObjectIntMap();

	@SuppressWarnings("unchecked")
	private final ArrayList readObjects = new ArrayList();

	public FixedReferenceResolver()
	{
	}

	@Override
	public void setKryo(Kryo kryo)
	{
	}

	@Override
	@SuppressWarnings("unchecked")
	public int addWrittenObject(Object object)
	{
		int id = writtenObjects.size;
		writtenObjects.put(object, id);
		return id;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int getWrittenId(Object object)
	{
		return writtenObjects.get(object, -1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int nextReadId(Class type)
	{
		int id = readObjects.size();
		readObjects.add(null);
		return id;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addReadObject(int id, Object object)
	{
		readObjects.set(id, object);
	}

	@Override
	public Object getReadObject(Class type, int id)
	{
		return readObjects.get(id);
	}

	@Override
	public void reset()
	{
		readObjects.clear();
		writtenObjects.clear(2048);
	}

	/**
	 * Returns false for all primitive wrappers.
	 */
	public boolean useReferences(Class type)
	{
		return !Util.isWrapperClass(type);
	}
}

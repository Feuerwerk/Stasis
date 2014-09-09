package de.boxxit.stasis;

import com.esotericsoftware.kryo.DefaultSerializer;
import de.boxxit.stasis.serializer.MethodResultSerializer;

/**
 * User: Christian Fruth
 */
@DefaultSerializer(MethodResultSerializer.class)
public class MethodResult
{
	public static enum Type { Void, Value, Exception }

	private Type type;
	private Object result;

	public MethodResult()
	{
		type = Type.Void;
	}

	public MethodResult(Object result)
	{
		this.result = result;
		type = Type.Value;
	}

	public MethodResult(Exception exception)
	{
		this.result = exception;
		type = Type.Exception;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public Object getResult()
	{
		return result;
	}

	public void setResult(Object result)
	{
		this.result = result;
	}
}

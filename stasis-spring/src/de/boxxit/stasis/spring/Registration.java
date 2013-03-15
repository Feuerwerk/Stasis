package de.boxxit.stasis.spring;

import com.esotericsoftware.kryo.Serializer;

/**
 * User: Christian Fruth
 */
public class Registration
{
	private Integer id;
	private Class<?> type;
	private Serializer<?> serializer;
	private Class<? extends Serializer<?>> serializerClass;

	public Registration()
	{
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Class<?> getType()
	{
		return type;
	}

	public void setType(Class<?> type)
	{
		this.type = type;
	}

	public Serializer<?> getSerializer()
	{
		return serializer;
	}

	public void setSerializer(Serializer<?> serializer)
	{
		this.serializer = serializer;
	}

	public Class<? extends Serializer<?>> getSerializerClass()
	{
		return serializerClass;
	}

	public void setSerializerClass(Class<? extends Serializer<?>> serializerClass)
	{
		this.serializerClass = serializerClass;
	}
}

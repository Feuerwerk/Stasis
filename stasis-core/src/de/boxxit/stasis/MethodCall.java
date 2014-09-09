package de.boxxit.stasis;

import com.esotericsoftware.kryo.DefaultSerializer;
import de.boxxit.stasis.serializer.MethodCallSerializer;

/**
 * User: Christian Fruth
 */
@DefaultSerializer(MethodCallSerializer.class)
public class MethodCall
{
	private String name;
	private boolean assumeAuthenticated;
	private Object[] args;

	public MethodCall()
	{
	}

	public MethodCall(String name, boolean assumeAuthenticated, Object[] args)
	{
		this.name = name;
		this.assumeAuthenticated = assumeAuthenticated;
		this.args = (args != null) ? args : new Object[0];
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isAssumeAuthenticated()
	{
		return assumeAuthenticated;
	}

	public void setAssumeAuthenticated(boolean assumeAuthenticated)
	{
		this.assumeAuthenticated = assumeAuthenticated;
	}

	public Object[] getArgs()
	{
		return args;
	}

	public void setArgs(Object[] args)
	{
		this.args = args;
	}
}

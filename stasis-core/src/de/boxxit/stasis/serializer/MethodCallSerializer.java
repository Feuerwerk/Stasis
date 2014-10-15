package de.boxxit.stasis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.MethodCall;

/**
 * User: Christian Fruth
 */
public class MethodCallSerializer extends Serializer<MethodCall>
{
	public MethodCallSerializer()
	{
		setAcceptsNull(false);
	}

	@Override
	public void write(Kryo kryo, Output output, MethodCall object)
	{
		Object[] args = object.getArgs();
		int argCount = args.length;

		output.writeString(object.getName());
		output.writeBoolean(object.isAssumeAuthenticated());
		output.writeInt(argCount, true);

		for (int i = 0; i < argCount; ++i)
		{
			kryo.writeClassAndObject(output, args[i]);
		}
	}

	@Override
	public MethodCall read(Kryo kryo, Input input, Class<MethodCall> type)
	{
		MethodCall methodCall = new MethodCall();
		kryo.reference(methodCall);

		String name = input.readString();
		boolean assumeAuthenticated = input.readBoolean();
		int argCount = input.readInt(true);
		Object[] args = new Object[argCount];

		for (int i = 0; i < argCount; ++i)
		{
			args[i] = kryo.readClassAndObject(input);
		}

		methodCall.setName(name);
		methodCall.setAssumeAuthenticated(assumeAuthenticated);
		methodCall.setArgs(args);

		return methodCall;
	}
}

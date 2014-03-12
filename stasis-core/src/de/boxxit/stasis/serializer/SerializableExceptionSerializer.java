package de.boxxit.stasis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.SerializableException;

/**
 * User: Christian Fruth
 */
public class SerializableExceptionSerializer extends Serializer<SerializableException>
{
	public SerializableExceptionSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, SerializableException object)
	{
		StackTraceElement[] stackTrace = object.getStackTrace();

		output.writeString(object.getType());
		output.writeString(object.getMessage());
		output.writeInt(stackTrace.length, true);

		for (StackTraceElement stackFrame : stackTrace)
		{
			output.writeString(stackFrame.getClassName());
			output.writeString(stackFrame.getMethodName());
			output.writeString(stackFrame.getFileName());
			output.writeInt(stackFrame.getLineNumber(), true);
		}
	}

	@Override
	public SerializableException read(Kryo kryo, Input input, Class<SerializableException> clazz)
	{
		String type = input.readString();
		String message = input.readString();
		int stackLength = input.readInt(true);
		StackTraceElement[] stackTrace = new StackTraceElement[stackLength];

		for (int i = 0; i < stackLength; ++i)
		{
			String className = input.readString();
			String methodName = input.readString();
			String fileName = input.readString();
			int lineNumber = input.readInt(true);
			stackTrace[i] = new StackTraceElement(className, methodName, fileName, lineNumber);
		}

		SerializableException ex = new SerializableException(type, message);
		ex.setStackTrace(stackTrace);
		return ex;
	}
}

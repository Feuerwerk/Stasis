package de.boxxit.stasis.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.MethodResult;

/**
 * User: Christian Fruth
 */
public class MethodResultSerializer extends Serializer<MethodResult>
{
	public MethodResultSerializer()
	{
	}

	@Override
	public void write(Kryo kryo, Output output, MethodResult object)
	{
		output.writeInt(object.getType().ordinal(), true);

		switch (object.getType())
		{
			case Void:
				break;

			case Value:
			case Exception:
				kryo.writeClassAndObject(output, object.getResult());
				break;
		}
	}

	@Override
	public MethodResult read(Kryo kryo, Input input, Class<MethodResult> type)
	{
		MethodResult methodResult = new MethodResult();
		kryo.reference(methodResult);

		MethodResult.Type resultType = MethodResult.Type.values()[input.readInt(true)];
		Object result = null;

		switch (resultType)
		{
			case Void:
				break;

			case Value:
			case Exception:
				result = kryo.readClassAndObject(input);
				break;
		}

		methodResult.setType(resultType);
		methodResult.setResult(result);

		return methodResult;
	}
}

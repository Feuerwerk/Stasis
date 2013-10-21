package de.boxxit.stasis.spring;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.AuthenticationMissmatchException;
import de.boxxit.stasis.AuthenticationResult;
import de.boxxit.stasis.SerializableException;
import de.boxxit.stasis.StasisConstants;
import de.boxxit.stasis.StasisUtils;
import de.boxxit.stasis.security.LoginService;
import de.boxxit.stasis.security.LoginStatus;
import de.boxxit.stasis.serializer.ArraysListSerializer;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * User: Christian Fruth
 */
public class StasisController implements Controller
{
	private static final String LOGIN_FUNCTION = "login";
	private static final Logger LOGGER = LoggerFactory.getLogger(StasisController.class);

	private static class InOut
	{
		public Input input;
		public Output output;
		public Kryo kryo;
	}

	private Map<String, Object> services;
	private List<Registration> registeredSerializers;
	private LoginService loginService;
	private ObjectPool<InOut> ioPool;
	private Class<? extends Serializer> defaultSerializer = null;
	private int serverVersion;

	// Ich bin noch ein Kommentar
	public StasisController()
	{
		final PoolableObjectFactory<InOut> poolableObjectFactory = new BasePoolableObjectFactory<InOut>()
		{
			@Override
			public InOut makeObject() throws Exception
			{
				InOut io = new InOut();

				io.input = new Input(4096);
				io.output = new Output(4096);
				io.kryo = new Kryo();

				io.kryo.addDefaultSerializer(Arrays.asList().getClass(), ArraysListSerializer.class);

				if (defaultSerializer != null)
				{
					io.kryo.setDefaultSerializer(defaultSerializer);
				}

				if (registeredSerializers != null)
				{
					for (Registration registration : registeredSerializers)
					{
						Serializer<?> serializer = registration.getSerializer();

						if ((serializer == null) && (registration.getSerializerClass() != null))
						{
							serializer = registration.getSerializerClass().newInstance();
						}

						if (registration.getId() == null)
						{
							assert (serializer != null);
							io.kryo.register(registration.getType(), serializer);
						}
						else if (serializer == null)
						{
							io.kryo.register(registration.getType(), registration.getId());
						}
						else
						{
							io.kryo.register(registration.getType(), serializer, registration.getId());
						}
					}
				}

				return io;
			}

			@Override
			public void passivateObject(InOut io) throws Exception
			{
				io.input.setInputStream(null);
				io.output.setOutputStream(null);
				super.passivateObject(io);
			}
		};

		ioPool = new StackObjectPool<InOut>(poolableObjectFactory);
	}

	public void setServerVersion(int serverVersion)
	{
		this.serverVersion = serverVersion;
	}

	public void setLoginService(LoginService loginService)
	{
		this.loginService = loginService;
	}

	public void setServices(Map<String, Object> services)
	{
		this.services = services;
	}

	public void setRegisteredSerializers(List<Registration> registeredSerializers)
	{
		this.registeredSerializers = registeredSerializers;
	}

	public void setDefaultSerializer(Class<? extends Serializer> defaultSerializer)
	{
		this.defaultSerializer = defaultSerializer;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		InOut io = ioPool.borrowObject();

		try
		{
			boolean gzipRequested = StasisUtils.isUsingGzipEncoding(request.getHeaders(StasisUtils.ACCEPT_ENCODING_KEY));
			boolean gzipUsed = StasisUtils.isUsingGzipEncoding(request.getHeaders(StasisUtils.CONTENT_ENCODING_KEY));
			InputStream inputStream = request.getInputStream();
			OutputStream outputStream = response.getOutputStream();

			if (gzipUsed)
			{
				inputStream = new GZIPInputStream(inputStream);
			}

			if (gzipRequested)
			{
				response.setHeader(StasisUtils.CONTENT_ENCODING_KEY, StasisUtils.GZIP_ENCODING);
				outputStream = new GZIPOutputStream(outputStream);
			}

			response.setContentType(StasisConstants.CONTENT_TYPE);

			io.input.setInputStream(inputStream);
			io.output.setOutputStream(outputStream);

			handleIO(io.kryo, io.input, io.output);

			//io.output.close();
			outputStream.close();
		}
		finally
		{
			ioPool.returnObject(io);
		}

		return null;
	}

	protected void handleIO(Kryo kryo, Input input, Output output) throws AuthenticationMissmatchException
	{
		// Funktionsnamen lesen
		long startTimeMillis = System.currentTimeMillis();
		String functionName;
		boolean assumeAuthenticated;
		Object[] args;
		Object[] result;
		boolean error = false;

		try
		{
			functionName = kryo.readObject(input, String.class);
			assumeAuthenticated = kryo.readObject(input, boolean.class);
			args = kryo.readObject(input, Object[].class);
		}
		catch (Exception ex)
		{
			LOGGER.error("Call failed because function name or arguments can't be read");
			throw ex;
		}

		try
		{
			if (LOGIN_FUNCTION.equals(functionName))
			{
				result = handleLogin(args);
			}
			else
			{
				result = handleServiceFunction(functionName, assumeAuthenticated, args);
			}
		}
		catch (AuthenticationMissmatchException ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("Call failed (name = %s, arguments = %s, duration = %dms, exception = %s)", functionName, formatArray(args), stopTimeMillis - startTimeMillis, ex.getClass().getName()));
			}

			error = true;
			result = new Object[] { ex };
		}
		catch (SerializableException ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("Call failed (name = %s, arguments = %s, duration = %dms, exception = %s:%s)", functionName, formatArray(args), stopTimeMillis - startTimeMillis, ex.getClass().getName(), ex.getLocalizedMessage()));
			}

			error = true;
			result = new Object[] { ex };
		}
		catch (Throwable ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();

				if (ex instanceof RuntimeException)
				{
					LOGGER.error(String.format("Call failed (name = %s, arguments = %s, duration = %dms)", functionName, formatArray(args), stopTimeMillis - startTimeMillis), ex);
				}
				else
				{
					LOGGER.error(String.format("Call failed (name = %s, arguments = %s, duration = %dms, exception = %s:%s)", functionName, formatArray(args), stopTimeMillis - startTimeMillis, ex.getClass().getName(), ex.getLocalizedMessage()));
				}
			}

			error = true;
			result = new Object[] { new SerializableException(ex) };
		}

		try
		{
			kryo.writeObject(output, result);

			output.flush();

			if (!error && LOGGER.isDebugEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.debug(String.format("Call succeeded (name = %s, arguments = %s, result = %s, duration = %dms)", functionName, formatArray(args), formatArray(result), stopTimeMillis - startTimeMillis));
			}
		}
		catch (Exception ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("Call failed because result can't be written back to client (name = %s, arguments = %s, result = %s, duration = %dms)", functionName, formatArray(args), formatArray(result), stopTimeMillis - startTimeMillis), ex);
			}
		}
	}

	private Object[] handleLogin(Object[] args)
	{
		AuthenticationResult authenticationResult;

		if (args == null)
		{
			throw new IllegalArgumentException("No arguments supplied for login");
		}

		if (args.length != 3)
		{
			throw new IllegalArgumentException("3 arguments for login expected");
		}

		String userName = (String)args[0];
		String password = (String)args[1];
		int clientVersion = (Integer)args[2];

		if (clientVersion != serverVersion)
		{
			authenticationResult = AuthenticationResult.VersionMissmatch;
		}
		else
		{
			LoginStatus loginStatus = loginService.login(userName, password);
			authenticationResult = loginStatus.isAuthenticated() ? AuthenticationResult.Authenticated : AuthenticationResult.Unauthenticated;
		}

		return new Object[] { authenticationResult };
	}

	private Object[] handleServiceFunction(String functionName, boolean assumeAuthenticated, Object[] args) throws Throwable
	{
		Method foundMethod = null;
		LoginStatus loginStatus = loginService.getStatus();

		if (loginStatus.isAuthenticated() != assumeAuthenticated)
		{
			throw new AuthenticationMissmatchException(loginStatus.isAuthenticated());
		}

		int index = functionName.indexOf('.');
		String serviceName = functionName.substring(0, index);
		Object service = services.get(serviceName);

		if (service == null)
		{
			throw new SerializableException("serviceMissing", "Can't find matching service");
		}

		Class<?> serviceClass = service.getClass();
		String serviceMethod = functionName.substring(index + 1);

		for (Method method : serviceClass.getMethods())
		{
			if (method.getName().equals(serviceMethod))
			{
				foundMethod = method;
				break;
			}
		}

		if (foundMethod == null)
		{
			throw new SerializableException("serviceFunctionMissing", "Can't find matching service function");
		}

		try
		{
			Class<?> returnType = foundMethod.getReturnType();

			if (void.class.equals(returnType))
			{
				foundMethod.invoke(service, args);
				return new Object[0];
			}

			Object returnValue = foundMethod.invoke(service, args);
			return new Object[] { returnValue };
		}
		catch (InvocationTargetException ex)
		{
			// Exception entpacken um ursächliche Exception näher auszuwerten
			throw ex.getTargetException();
		}
	}

	private CharSequence formatArray(Object[] array)
	{
		if (array == null)
		{
			return "null";
		}

		StringBuilder str = new StringBuilder("{ ");
		int length = Math.min(10, array.length);

		for (int i = 0; i < length; ++i)
		{
			if (i != 0)
			{
				str.append(", ");
			}

			str.append(array[i]);
		}

		if (length < array.length)
		{
			str.append(", and ");
			str.append(array.length - length);
			str.append("more");
		}

		str.append(" }");

		return str;
	}
}

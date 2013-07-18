package de.boxxit.stasis.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.AuthenticationMissmatchException;
import de.boxxit.stasis.SerializableException;
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
			io.input.setInputStream(request.getInputStream());
			io.output.setOutputStream(response.getOutputStream());

			handleIO(io.kryo, io.input, io.output);
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
		String name;

		try
		{
			name = kryo.readObject(input, String.class);
		}
		catch (Exception ex)
		{
			LOGGER.error("Call failed because function name can't be read");
			throw ex;
		}

		if (LOGIN_FUNCTION.equals(name))
		{
			String userName;
			String password;

			try
			{
				userName = kryo.readObject(input, String.class);
				password = kryo.readObject(input, String.class);
			}
			catch (Exception ex)
			{
				if (LOGGER.isErrorEnabled())
				{
					LOGGER.error(String.format("Call failed because arguments can't be read (name = %s)", name));
				}

				throw ex;
			}

			LoginStatus loginStatus;

			try
			{
				loginStatus = loginService.login(userName, password);
			}
			catch (Exception ex)
			{
				if (LOGGER.isErrorEnabled())
				{
					long stopTimeMillis = System.currentTimeMillis();
					LOGGER.error(String.format("Call failed because arguments can't be read (name = %s, arguments = %s, duration = %dms)", name, userName, stopTimeMillis - startTimeMillis), ex);
				}

				throw ex;
			}

			try
			{
				kryo.writeObject(output, loginStatus.isAuthenticated());

				output.flush();
			}
			catch (Exception ex)
			{
				if (LOGGER.isErrorEnabled())
				{
					long stopTimeMillis = System.currentTimeMillis();
					LOGGER.error(String.format("Call failed because result can't be written back to client (name = %s, arguments = %s, result = %s, duration = %dms)", name, userName, loginStatus.isAuthenticated(), stopTimeMillis - startTimeMillis), ex);
				}

				throw ex;
			}
		}
		else
		{
			Object[] args = null;
			Object[] result = null;
			Object service = null;
			Method foundMethod = null;

			try
			{
				try
				{
					LoginStatus loginStatus = loginService.getStatus();
					boolean assumeAuthenticated = kryo.readObject(input, boolean.class);

					if (loginStatus.isAuthenticated() != assumeAuthenticated)
					{
						throw new AuthenticationMissmatchException(loginStatus.isAuthenticated());
					}

					int index = name.indexOf('.');
					String serviceName = name.substring(0, index);
					service = services.get(serviceName);

					if (service == null)
					{
						throw new SerializableException("serviceMissing", "Can't find matching service");
					}

					Class<?> serviceClass = service.getClass();
					String serviceMethod = name.substring(index + 1);

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

					args = kryo.readObject(input, Object[].class);
				}
				catch (SerializableException | AuthenticationMissmatchException ex)
				{
					throw ex;
				}
				catch (Exception ex)
				{
					if (LOGGER.isErrorEnabled())
					{
						LOGGER.error(String.format("Call failed because arguments can't be read (name = %s)", name));
					}

					throw ex;
				}

				try
				{
					Class<?> returnType = foundMethod.getReturnType();

					if (void.class.equals(returnType))
					{
						foundMethod.invoke(service, args);
						result = new Object[0];
					}
					else
					{
						Object returnValue = foundMethod.invoke(service, args);
						result = new Object[] { returnValue };
					}
				}
				catch (IllegalAccessException ex)
				{
					if (LOGGER.isErrorEnabled())
					{
						long stopTimeMillis = System.currentTimeMillis();
						LOGGER.error(String.format("Call failed (name = %s, arguments = %s, duration = %dms)", name, formatArray(args), stopTimeMillis - startTimeMillis), ex);
					}

					throw ex;
				}
				catch (InvocationTargetException ex)
				{
					// Exception entpacken um ursächliche Exception näher auszuwerten
					Throwable targetException = ex.getTargetException();

					if (LOGGER.isErrorEnabled())
					{
						long stopTimeMillis = System.currentTimeMillis();
						LOGGER.error(String.format("Call failed (name = %s, arguments = %s, duration = %dms)", name, formatArray(args), stopTimeMillis - startTimeMillis), targetException);
					}

					throw targetException;
				}
			}
			catch (AuthenticationMissmatchException | SerializableException ex)
			{
				result = new Object[] { ex };
			}
			catch (Throwable ex)
			{
				result = new Object[] { new SerializableException(ex) };
			}

			try
			{
				kryo.writeObject(output, result);

				output.flush();
			}
			catch (Exception ex)
			{
				if (LOGGER.isErrorEnabled())
				{
					long stopTimeMillis = System.currentTimeMillis();
					LOGGER.error(String.format("Call failed because result can't be written back to client (name = %s, arguments = %s, result = %s, duration = %dms)", name, formatArray(args), formatArray(result), stopTimeMillis - startTimeMillis), ex);
				}
			}

			if (LOGGER.isDebugEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.debug(String.format("Call succeeded (name = %s, arguments = %s, result = %s, duration = %dms)", name, formatArray(args), formatArray(result), stopTimeMillis - startTimeMillis));
			}
		}
	}

	private CharSequence formatArray(Object[] array)
	{
		if (array == null)
		{
			return "null";
		}

		StringBuilder str = new StringBuilder("{ ");

		for (int i = 0, length = array.length; i < length; ++i)
		{
			if (i != 0)
			{
				str.append(", ");
			}

			str.append(array[i]);
		}

		str.append(" }");

		return str;
	}
}

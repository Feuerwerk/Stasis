package de.boxxit.statis.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.statis.AuthenticationMissmatchException;
import de.boxxit.statis.SerializableException;
import de.boxxit.statis.security.LoginService;
import de.boxxit.statis.security.LoginStatus;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * User: Christian Fruth
 */
public class StasisController implements Controller
{
	private static final String LOGIN_FUNCTION = "login";

	private static class InOut
	{
		public Input input;
		public Output output;
		public Kryo kryo;
	}

	private Map<String, Object> services;
	private Map<Class<?>, Serializer<?>> registeredSerializers;
	private LoginService loginService;
	private ObjectPool<InOut> ioPool;

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

				if (registeredSerializers != null)
				{
					for (Map.Entry<Class<?>, Serializer<?>> serializerEntry : registeredSerializers.entrySet())
					{
						io.kryo.register(serializerEntry.getKey(), serializerEntry.getValue());
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

		ioPool = new StackObjectPool<>(poolableObjectFactory);
	}

	public void setLoginService(LoginService loginService)
	{
		this.loginService = loginService;
	}

	public void setServices(Map<String, Object> services)
	{
		this.services = services;
	}

	public void setRegisteredSerializers(Map<Class<?>, Serializer<?>> registeredSerializers)
	{
		this.registeredSerializers = registeredSerializers;
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

	protected void handleIO(Kryo kryo, Input input, Output output) throws Exception
	{
		// Funktionsnamen lesen
		String name = kryo.readObject(input, String.class);

		if (LOGIN_FUNCTION.equals(name))
		{
			String userName = kryo.readObject(input, String.class);
			String password = kryo.readObject(input, String.class);

			LoginStatus loginStatus = loginService.login(userName, password);

			kryo.writeObject(output, loginStatus.isAuthenticated());

			output.flush();
		}
		else
		{
			Object[] result;

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
				Object service = services.get(serviceName);

				if (service == null)
				{
					throw new SerializableException("serviceMissing", "Can't find matching service");
				}

				Class<?> serviceClass = service.getClass();
				String serviceMethod = name.substring(index + 1);
				Method foundMethod = null;

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

				Object[] args = kryo.readObject(input, Object[].class);

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
				catch (InvocationTargetException ex)
				{
					// Exception entpacken um ursächliche Exception näher auszuwerten
					ex.printStackTrace();
					throw ex.getTargetException();
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

			kryo.writeObject(output, result);

			output.flush();
		}
	}
}

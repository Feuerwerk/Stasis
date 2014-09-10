package de.boxxit.stasis.spring;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import de.boxxit.stasis.AuthenticationMissmatchException;
import de.boxxit.stasis.AuthenticationResult;
import de.boxxit.stasis.LoginResult;
import de.boxxit.stasis.MessageException;
import de.boxxit.stasis.MethodCall;
import de.boxxit.stasis.MethodResult;
import de.boxxit.stasis.SerializableException;
import de.boxxit.stasis.StasisConstants;
import de.boxxit.stasis.StasisUtils;
import de.boxxit.stasis.security.LoginException;
import de.boxxit.stasis.security.LoginService;
import de.boxxit.stasis.security.LoginStatus;
import de.boxxit.stasis.serializer.ArraysListSerializer;
import de.boxxit.stasis.serializer.CollectionsSerializers;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * User: Christian Fruth
 */
public class StasisControllerV3 implements Controller, ApplicationContextAware, BeanNameAware
{
	private static final String LOGIN_FUNCTION = "login";
	private static final String LOGOUT_FUNCTION = "logout";
	private static final Logger LOGGER = LoggerFactory.getLogger(StasisControllerV3.class);

	private static class InOut
	{
		public Input input;
		public Output output;
		public Kryo kryo;

		private InOut()
		{
		}
	}

	private Map<String, Object> services;
	private List<String> serviceNames;
	private List<Registration> registeredSerializers;
	private LoginService loginService;
	private ObjectPool<InOut> ioPool;
	private Class<? extends Serializer<?>> defaultSerializer = null;
	private ApplicationContext applicationContext;
	private LoginValidator loginValidator;
	private String name;

	public StasisControllerV3()
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
				io.kryo.addDefaultSerializer(TreeMap.class, MapSerializer.class);
				io.kryo.addDefaultSerializer(Collections.unmodifiableList(new ArrayList<>()).getClass(), CollectionsSerializers.UnmodifiableListSerializer.class);
				io.kryo.addDefaultSerializer(Collections.unmodifiableList(new LinkedList<>()).getClass(), CollectionsSerializers.UnmodifiableListSerializer.class);
				io.kryo.register(MethodCall.class, 10);
				io.kryo.register(MethodResult.class, 11);

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

		ioPool = new StackObjectPool<>(poolableObjectFactory);
	}

	public void setLoginValidator(LoginValidator loginValidator)
	{
		this.loginValidator = loginValidator;
	}

	public void setLoginService(LoginService loginService)
	{
		this.loginService = loginService;
	}

	public void setServices(Map<String, Object> services)
	{
		this.services = services;
	}

	public void setServiceNames(List<String> serviceNames)
	{
		this.serviceNames = serviceNames;
	}

	public void setRegisteredSerializers(List<Registration> registeredSerializers)
	{
		this.registeredSerializers = registeredSerializers;
	}

	public void setDefaultSerializer(Class<? extends Serializer<?>> defaultSerializer)
	{
		this.defaultSerializer = defaultSerializer;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	@Override
	public void setBeanName(String name)
	{
		this.name = name;
	}

	@PostConstruct
	public void init()
	{
		if (services == null)
		{
			services = new HashMap<>();
		}

		if (serviceNames != null)
		{
			for (String serviceName : serviceNames)
			{
				Object service = applicationContext.getBean(serviceName);
				services.put(serviceName, service);
			}
		}
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

			handleIO(io.kryo, io.input, io.output, request);

			//io.output.close();
			outputStream.close();
		}
		finally
		{
			ioPool.returnObject(io);
		}

		return null;
	}

	protected void handleIO(Kryo kryo, Input input, Output output, HttpServletRequest request) throws AuthenticationMissmatchException
	{
		// Funktionsnamen lesen
		long startTimeMillis = System.currentTimeMillis();
		MethodCall methodCall;
		MethodResult methodResult;
		boolean error = false;
		Object[] args;

		try
		{
			methodCall = kryo.readObject(input, MethodCall.class);
			args = methodCall.getArgs();
		}
		catch (Exception ex)
		{
			LOGGER.error(String.format("%s: Call failed because function name or arguments can't be read", name), ex);
			throw ex;
		}

		try
		{
			switch (methodCall.getName())
			{
				case LOGIN_FUNCTION:
					methodResult = handleLogin(args);
					args = new Object[] { args[0], "######", args[2] }; // Das Passwort darf nicht geloggt werden
					break;

				case LOGOUT_FUNCTION:
					methodResult = handleLogout(request);
					break;

				default:
					methodResult = handleServiceFunction(methodCall.getName(), methodCall.isAssumeAuthenticated(), args);
					break;
			}
		}
		catch (LoginException ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("%s: Call failed because of login exception (name = %s, arguments = %s, duration = %dms, exception = %s)", name, methodCall.getName(), formatArray(args), stopTimeMillis - startTimeMillis, ex.getMessage()));
			}

			error = true;
			methodResult = new MethodResult(new SerializableException(ex));
		}
		catch (AuthenticationMissmatchException ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("%s: Call failed because of authentication missmatch (name = %s, arguments = %s, duration = %dms, exception = %s)", name, methodCall.getName(), formatArray(args), stopTimeMillis - startTimeMillis, ex.getClass().getName()));
			}

			error = true;
			methodResult = new MethodResult(ex);
		}
		catch (MessageException ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("%s: Call failed because of message exception (name = %s, arguments = %s, duration = %dms, exception = %s:%s)", name, methodCall.getName(), formatArray(args), stopTimeMillis - startTimeMillis, ex.getId(), ex.getMessage()));
			}

			error = true;
			methodResult = new MethodResult(ex);
		}
		catch (Throwable ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("%s: Call failed because of throwable exception (name = %s, arguments = %s, duration = %dms)", name, methodCall.getName(), formatArray(args), stopTimeMillis - startTimeMillis), ex);
			}

			error = true;
			methodResult = new MethodResult(new SerializableException(ex));
		}

		try
		{
			kryo.writeObject(output, methodResult);

			output.flush();

			if (!error && LOGGER.isDebugEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.debug(String.format("%s: Call succeeded (name = %s, arguments = %s, result = %s, duration = %dms)", name, methodCall.getName(), formatArray(args), methodResult.getResult(), stopTimeMillis - startTimeMillis));
			}
		}
		catch (Exception ex)
		{
			if (LOGGER.isErrorEnabled())
			{
				long stopTimeMillis = System.currentTimeMillis();
				LOGGER.error(String.format("%s: Call failed because result can't be written back to client (name = %s, arguments = %s, result = %s, duration = %dms)", name, methodCall.getName(), formatArray(args), methodResult.getResult(), stopTimeMillis - startTimeMillis), ex);
			}
		}
	}

	private MethodResult handleLogin(Object[] args) throws LoginException
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
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, Object> payload = (Map)args[2];
		Map<String, Object> result = new HashMap<>();

		if ((loginValidator == null) || loginValidator.preAuthenticate(payload, result))
		{
			LoginStatus loginStatus = loginService.login(userName, password);

			if ((loginValidator == null) || loginValidator.postAuthenticate(loginStatus, payload, result))
			{
				authenticationResult = loginStatus.isAuthenticated() ? AuthenticationResult.Authenticated : AuthenticationResult.Unauthenticated;
			}
			else
			{
				authenticationResult = AuthenticationResult.Invalid;
			}
		}
		else
		{
			authenticationResult = AuthenticationResult.Invalid;
		}

		return new MethodResult(new LoginResult(authenticationResult, result));
	}

	private MethodResult handleLogout(HttpServletRequest request) throws LoginException
	{
		loginService.logout();

		// HTTP-Sitzung für ungültig erklären
		HttpSession session = request.getSession(false);

		if (session != null)
		{
			session.invalidate();
		}

		return new MethodResult();
	}

	private MethodResult handleServiceFunction(String functionName, boolean assumeAuthenticated, Object[] args) throws Throwable
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
				return new MethodResult();
			}

			Object returnValue = foundMethod.invoke(service, args);
			return new MethodResult(returnValue);
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

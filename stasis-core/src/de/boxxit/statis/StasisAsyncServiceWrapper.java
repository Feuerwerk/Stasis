package de.boxxit.statis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.util.Arrays;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: Christian Fruth
 */
public class StasisAsyncServiceWrapper implements FactoryBean<Object>, InitializingBean
{
	private static class InvocationHandlerImpl implements InvocationHandler
	{
		private RemoteConnection connection;
		private String serviceName;

		private InvocationHandlerImpl(RemoteConnection connection, String serviceName)
		{
			this.connection = connection;
			this.serviceName = serviceName;
		}

		@Override
		public Object invoke(final Object proxy, Method method, Object[] args) throws Throwable
		{
			if (args.length < 1)
			{
				throw new RuntimeException("At least one argument is required");
			}

			Object lastArg = args[args.length - 1];

			if (!(lastArg instanceof ResultHandler))
			{
				throw new RuntimeException("Last argument must be de.boxxit.stasis.ResultHandler");
			}

			Object[] argsSync = Arrays.copyOf(args, args.length - 1);
			@SuppressWarnings("unchecked")
			final ResultHandler<? super Object> eventHandler = (ResultHandler)lastArg;
			String name = serviceName + "." + method.getName();

			connection.callAsync(new CallHandler<Object>()
			{
				@Override
				public void succeeded(Object value)
				{
					eventHandler.handle(value);
				}

				@Override
				public void failed(Exception ex)
				{
					showError(ex);
				}
			}, name, argsSync);
			return null;
		}

		protected void showError(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public StasisAsyncServiceWrapper()
	{
	}

	private Class<?> serviceInterface;
	private RemoteConnection connection;
	private Object serviceProxy;
	private String serviceName;

	public Class<?> getServiceInterface()
	{
		return serviceInterface;
	}

	public void setServiceInterface(Class<?> serviceInterface)
	{
		this.serviceInterface = serviceInterface;
	}

	public void setConnection(RemoteConnection connection)
	{
		this.connection = connection;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	@Override
	public Object getObject()
	{
		return serviceProxy;
	}

	@Override
	public Class<?> getObjectType()
	{
		return serviceInterface;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

	@Override
	public void afterPropertiesSet() throws MalformedURLException
	{
		serviceProxy = create(serviceInterface, connection, serviceName);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> serviceInterface, RemoteConnection connection, String serviceName)
	{
		return (T)Proxy.newProxyInstance(StasisAsyncServiceWrapper.class.getClassLoader(), new Class[] { serviceInterface }, new InvocationHandlerImpl(connection, serviceName));
	}
}

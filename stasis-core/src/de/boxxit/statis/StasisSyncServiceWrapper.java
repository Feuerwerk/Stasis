package de.boxxit.statis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * User: Christian Fruth
 */
public class StasisSyncServiceWrapper
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
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			return connection.callSync(serviceName + "." + method.getName(), args);
		}
	}

	private StasisSyncServiceWrapper()
	{
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> serviceInterface, RemoteConnection connection, String serviceName)
	{
		return (T)Proxy.newProxyInstance(StasisAsyncServiceWrapper.class.getClassLoader(), new Class[] { serviceInterface }, new InvocationHandlerImpl(connection, serviceName));
	}
}

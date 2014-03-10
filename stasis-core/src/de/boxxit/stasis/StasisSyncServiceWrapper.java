package de.boxxit.stasis;

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
		private final Method toStringMethod;
		private final Method equalsMethod;
		private RemoteConnection connection;
		private String serviceName;

		private InvocationHandlerImpl(RemoteConnection connection, String serviceName)
		{
			this.connection = connection;
			this.serviceName = serviceName;

			try
			{
				equalsMethod = getClass().getMethod("equals", Object.class);
				toStringMethod = getClass().getMethod("toString");
			}
			catch (NoSuchMethodException ex)
			{
				throw new RuntimeException(ex);
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			if (method.equals(equalsMethod))
			{
				return serviceEquals(proxy, args[0]);
			}

			if (method.equals(toStringMethod))
			{
				return serviceToString(proxy);
			}

			return connection.callSync(serviceName + "." + method.getName(), args);
		}

		private boolean serviceEquals(Object proxy, Object obj)
		{
			return proxy == obj;
		}

		private String serviceToString(Object proxy)
		{
			return "Sync(" + serviceName + ")";
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

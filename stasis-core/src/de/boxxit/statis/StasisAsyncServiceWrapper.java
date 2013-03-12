package de.boxxit.statis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * User: Christian Fruth
 */
public class StasisAsyncServiceWrapper
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

	private StasisAsyncServiceWrapper()
	{
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> serviceInterface, RemoteConnection connection, String serviceName)
	{
		return (T)Proxy.newProxyInstance(StasisAsyncServiceWrapper.class.getClassLoader(), new Class[] { serviceInterface }, new InvocationHandlerImpl(connection, serviceName));
	}
}

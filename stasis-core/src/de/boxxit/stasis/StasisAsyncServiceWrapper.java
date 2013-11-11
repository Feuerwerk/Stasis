package de.boxxit.stasis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * User: Christian Fruth
 */
public class StasisAsyncServiceWrapper
{
	private static class CallHandlerImpl implements CallHandler<Object>, ErrorChain
	{
		private ResultHandler<? super Object> eventHandler;
		private ErrorHandler defaultErrorHandler;

		private CallHandlerImpl(ResultHandler<? super Object> eventHandler, ErrorHandler defaultErrorHandler)
		{
			this.eventHandler = eventHandler;
			this.defaultErrorHandler = defaultErrorHandler;
		}

		@Override
		public void succeeded(Object value)
		{
			eventHandler.handle(value);
		}

		@Override
		public void failed(Exception ex)
		{
			ex.printStackTrace();

			if (eventHandler instanceof ErrorHandler)
			{
				((ErrorHandler)eventHandler).failed(ex, this);
			}
			else if (defaultErrorHandler != null)
			{
				ErrorHandler errorHandler = defaultErrorHandler;
				defaultErrorHandler = null;
				errorHandler.failed(ex, this);
			}
		}

		@Override
		public void handleError(Exception ex)
		{
			if (defaultErrorHandler != null)
			{
				ErrorHandler errorHandler = defaultErrorHandler;
				defaultErrorHandler = null;
				errorHandler.failed(ex, this);
			}
		}
	}

	private static class InvocationHandlerImpl implements InvocationHandler
	{
		private RemoteConnection connection;
		private String serviceName;
		private ErrorHandler defaultErrorHandler;

		private InvocationHandlerImpl(RemoteConnection connection, String serviceName, ErrorHandler defaultErrorHandler)
		{
			this.connection = connection;
			this.serviceName = serviceName;
			this.defaultErrorHandler = defaultErrorHandler;
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
			ResultHandler<? super Object> eventHandler = (ResultHandler)lastArg;
			String name = serviceName + "." + method.getName();

			connection.callAsync(new CallHandlerImpl(eventHandler, defaultErrorHandler), name, argsSync);
			return null;
		}
	}

	private StasisAsyncServiceWrapper()
	{
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> serviceInterface, RemoteConnection connection, String serviceName, ErrorHandler defaultErrorHandler)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return (T)Proxy.newProxyInstance(classLoader, new Class[] { serviceInterface }, new InvocationHandlerImpl(connection, serviceName, defaultErrorHandler));
	}
}

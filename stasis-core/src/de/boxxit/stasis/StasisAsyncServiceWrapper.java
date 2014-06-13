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
		private AsyncServiceDelegate delegate;

		private CallHandlerImpl(ResultHandler<? super Object> eventHandler, ErrorHandler defaultErrorHandler, AsyncServiceDelegate delegate)
		{
			this.eventHandler = eventHandler;
			this.defaultErrorHandler = defaultErrorHandler;
			this.delegate = delegate;
		}

		@Override
		public void callWillBegin()
		{
			if (delegate != null)
			{
				delegate.serviceCallWillBegin();
			}
		}

		@Override
		public void callDidFinish()
		{
			if (delegate != null)
			{
				delegate.serviceCallDidFinish();
			}
		}

		@Override
		public void callSucceeded(Object value)
		{
			eventHandler.handle(value);
		}

		@Override
		public void callFailed(Exception ex)
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

			if (delegate != null)
			{
				delegate.serviceCallFailed(ex);
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
		private final Method toStringMethod;
		private final Method equalsMethod;
		private RemoteConnection connection;
		private String serviceName;
		private ErrorHandler defaultErrorHandler;
		private AsyncServiceDelegate delegate;

		private InvocationHandlerImpl(RemoteConnection connection, String serviceName, ErrorHandler defaultErrorHandler)
		{
			this.connection = connection;
			this.serviceName = serviceName;
			this.defaultErrorHandler = defaultErrorHandler;

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

		private InvocationHandlerImpl(RemoteConnection connection, String serviceName, AsyncServiceDelegate delegate)
		{
			this.connection = connection;
			this.serviceName = serviceName;
			this.delegate = delegate;

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
		public Object invoke(final Object proxy, Method method, Object[] args) throws Throwable
		{
			if (method.equals(equalsMethod))
			{
				return serviceEquals(proxy, args[0]);
			}

			if (method.equals(toStringMethod))
			{
				return serviceToString(proxy);
			}

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

			connection.callAsync(new CallHandlerImpl(eventHandler, defaultErrorHandler, delegate), name, argsSync);
			return null;
		}

		private boolean serviceEquals(Object proxy, Object obj)
		{
			return proxy == obj;
		}

		private String serviceToString(Object proxy)
		{
			return "Async(" + serviceName + ")";
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

	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> serviceInterface, RemoteConnection connection, String serviceName, AsyncServiceDelegate delegate)
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return (T)Proxy.newProxyInstance(classLoader, new Class[] { serviceInterface }, new InvocationHandlerImpl(connection, serviceName, delegate));
	}
}

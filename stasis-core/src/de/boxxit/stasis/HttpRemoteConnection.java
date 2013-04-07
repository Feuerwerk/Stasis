package de.boxxit.stasis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * User: Christian Fruth
 */
public class HttpRemoteConnection extends RemoteConnection
{
	private static final String CONTENT_TYPE_KEY = "Content-Type";
	private static final String CONTENT_TYPE_VALUE = "application/x-stasis";
	private static final String REQUEST_METHOD = "POST";
	private static final String LOGIN_FUNCTION = "login";

	protected abstract static class Call<T>
	{
		public CallHandler<T> handler;

		public void succeed(final T value, Synchronizer synchronizer)
		{
			synchronizer.runLater(new Runnable()
			{
				@Override
				public void run()
				{
					handler.succeeded(value);
				}
			});
		}

		public void fail(final Exception ex, Synchronizer synchronizer)
		{
			synchronizer.runLater(new Runnable()
			{
				@Override
				public void run()
				{
					handler.failed(ex);
				}
			});
		}

		public abstract void execute(ConnectionWorker worker);
	}

	protected static class LoginCall extends Call<Void>
	{
		public String userName;
		public String password;

		public LoginCall()
		{
		}

		@Override
		public void execute(ConnectionWorker worker)
		{
			worker.getConnection().invokeLogin(this);
		}
	}

	protected static class FunctionCall extends Call<Object>
	{
		public String name;
		public Object[] args;

		public FunctionCall()
		{
		}

		@Override
		public void execute(ConnectionWorker worker)
		{
			worker.getConnection().invokeFunction(this);
		}
	}

	private class ConnectionWorker implements Runnable
	{
		private ConnectionWorker()
		{
		}

		public HttpRemoteConnection getConnection()
		{
			return HttpRemoteConnection.this;
		}

		@Override
		public void run()
		{
			try
			{
				while (true)
				{
					Call<?> call = pendingCalls.take();
					call.execute(this);
				}
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}

			System.out.println("ConnectionWorker beendet");
		}
	}

	private URL url;
	private final Kryo kryo = new Kryo();
	private final Output output = new Output(4096);
	private final Input input = new Input(4096);
	private final BlockingQueue<Call<?>> pendingCalls = new LinkedBlockingQueue<Call<?>>();
	private final ResourceBundle resourceBundle = ResourceBundle.getBundle(HttpRemoteConnection.class.getPackage().getName() + ".errors");
	private CookieManager cookieManager = new CookieManager();
	private String activeUserName;
	private String activePassword;

	{
		// passenden Synchronizer finden
		Iterator<Synchronizer> serviceIter = ServiceLoader.load(Synchronizer.class).iterator();

		if (serviceIter.hasNext())
		{
			synchronizer = serviceIter.next();
		}

		// Kommunikations-Thread starten
		Thread workerThread = new Thread(new ConnectionWorker(), "Statis:HttpRemoteConnection");
		workerThread.setDaemon(true);
		workerThread.start();
	}

	public HttpRemoteConnection(URL url)
	{
		this.url = url;
		this.state = ConnectionState.Unconnected;

	}

	public CookieManager getCookieManager()
	{
		return cookieManager;
	}

	public void setCookieManager(CookieManager cookieManager)
	{
		this.cookieManager = cookieManager;
	}

	@Override
	public void setDefaultSerializer(Class<? extends Serializer> defaultSerializer)
	{
		kryo.setDefaultSerializer(defaultSerializer);
	}

	@Override
	public <T> void register(Class<T> type, int id)
	{
		kryo.register(type, id);
	}

	@Override
	public <T> void register(Class<T> type, Serializer<T> serializer)
	{
		kryo.register(type, serializer);
	}

	@Override
	public <T> void register(Class<T> type, Serializer<T> serializer, int id)
	{
		kryo.register(type, serializer, id);
	}

	@Override
	public void login(CallHandler<Void> handler, String userName, String password)
	{
		LoginCall newCall = new LoginCall();

		newCall.userName = userName;
		newCall.password = password;
		newCall.handler = handler;

		pendingCalls.add(newCall);
	}

	@SuppressWarnings("unchecked")
	public <T> void callAsync(CallHandler<T> handler, String name, Object... args)
	{
		FunctionCall newCall = new FunctionCall();

		newCall.name = name;
		newCall.args = args;
		newCall.handler = (CallHandler)handler;

		pendingCalls.add(newCall);
	}

	@Override
	public <T> T callSync(String name, Object... args) throws Exception
	{
		return invokeFunction(name, args);
	}

	protected void invokeLogin(LoginCall call)
	{
		try
		{
			boolean authenticated = invokeLogin(call.userName, call.password);

			if (!authenticated)
			{
				throw createException("loginFailed");
			}

			call.succeed(null, synchronizer);
		}
		catch (Exception ex)
		{
			call.fail(ex, synchronizer);
		}
	}

	protected boolean invokeLogin(String userName, String password) throws Exception
	{
		HttpURLConnection connection = null;

		try
		{
			connection = prepareConnection();

			// Service-Namen und Parameter an den Server schreiben
			output.setOutputStream(connection.getOutputStream());

			kryo.writeObject(output, LOGIN_FUNCTION);
			kryo.writeObject(output, userName);
			kryo.writeObject(output, password);

			output.close();
			output.setOutputStream(null);

			// Rückgabewert in Empfang nehmen
			cookieManager.storeCookies(connection);

			input.setInputStream(connection.getInputStream());

			boolean authenticated = kryo.readObject(input, boolean.class);

			if (authenticated)
			{
				this.activeUserName = userName;
				this.activePassword = password;
				this.state = ConnectionState.Authenticated;
			}

			return authenticated;
		}
		finally
		{
			output.close();
			output.setOutputStream(null);

			input.close();
			input.setInputStream(null);

			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	protected void invokeFunction(FunctionCall call)
	{
		try
		{
			if ((state != ConnectionState.Authenticated) && (userName != null) && (password != null))
			{
				boolean authenticated = invokeLogin(userName, password);

				if (!authenticated)
				{
					throw createException("loginFailed");
				}
			}

			try
			{
				Object returnValue = internalCall(call.name, call.args);
				call.succeed(returnValue, synchronizer);
			}
			catch (AuthenticationMissmatchException ex)
			{
				assert activeUserName != null : "activeUserName is null";
				assert activePassword != null : "activePassword is null";

				boolean authenticated = invokeLogin(activeUserName, activePassword);

				if (!authenticated)
				{
					throw createException("loginRepeated");
				}

				Object returnValue = internalCall(call.name, call.args);
				call.succeed(returnValue, synchronizer);
			}
		}
		catch (Exception ex)
		{
			call.fail(ex, synchronizer);
		}
	}

	protected <T> T invokeFunction(String name, Object[] args) throws Exception
	{
		if ((state != ConnectionState.Authenticated) && (userName != null) && (password != null))
		{
			boolean authenticated = invokeLogin(userName, password);

			if (!authenticated)
			{
				throw createException("loginFailed");
			}
		}

		try
		{
			@SuppressWarnings("unchecked") T result = (T)internalCall(name, args);
			return result;
		}
		catch (AuthenticationMissmatchException ex)
		{
			assert activeUserName != null : "activeUserName is null";
			assert activePassword != null : "activePassword is null";

			boolean authenticated = invokeLogin(activeUserName, activePassword);

			if (!authenticated)
			{
				throw createException("loginRepeated");
			}

			@SuppressWarnings("unchecked") T result = (T)internalCall(name, args);
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T internalCall(String name, Object[] args) throws Exception
	{
		HttpURLConnection connection = null;

		try
		{
			connection = prepareConnection();

			// Service-Namen und Parameter an den Server schreiben
			output.setOutputStream(connection.getOutputStream());

			kryo.writeObject(output, name);
			kryo.writeObject(output, state == ConnectionState.Authenticated); // Dem Server mitteilen ob wir davon ausgehen, dass wir bereits authentifiziert sind
			kryo.writeObject(output, args != null ? args : new Object[0]);

			// Ausgabe schließen
			output.close();
			output.setOutputStream(null);

			// Rückgabewert in Empfang nehmen
			cookieManager.storeCookies(connection);

			input.setInputStream(connection.getInputStream());

			Object[] result = kryo.readObject(input, Object[].class);

			if (result.length == 0)
			{
				return null;
			}

			if (result[0] instanceof Exception)
			{
				throw (Exception)result[0];
			}

			return (T)result[0];
		}
		finally
		{
			output.close();
			output.setOutputStream(null);

			input.close();
			input.setInputStream(null);

			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	protected HttpURLConnection prepareConnection() throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod(REQUEST_METHOD);
		connection.setRequestProperty(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		cookieManager.setCookies(connection);

		return connection;
	}

	protected StasisException createException(String id)
	{
		String message = resourceBundle.getString(id);
		return new StasisException(id, message);
	}
}

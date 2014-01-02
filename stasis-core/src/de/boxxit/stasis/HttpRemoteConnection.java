package de.boxxit.stasis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.stasis.serializer.ArraysListSerializer;
import de.boxxit.stasis.serializer.CollectionsSerializers;

/**
 * User: Christian Fruth
 */
public class HttpRemoteConnection extends RemoteConnection
{
	private static final String CONTENT_TYPE_KEY = "Content-Type";
	private static final String REQUEST_METHOD = "POST";
	private static final String LOGIN_FUNCTION = "login";

	protected abstract static class Call<T>
	{
		public CallHandler<T> handler;

		protected Call()
		{
		}

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
		public int clientVersion;

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
		}
	}

	private URL url;
	private final Kryo kryo;
	private final Output output = new Output(4096);
	private final Input input = new Input(4096);
	private final BlockingQueue<Call<?>> pendingCalls = new LinkedBlockingQueue<Call<?>>();
	private final ResourceBundle resourceBundle = ResourceBundle.getBundle(HttpRemoteConnection.class.getPackage().getName() + ".errors");
	private CookieManager cookieManager = new CookieManager();
	private String activeUserName;
	private String activePassword;
	private boolean gzipAvailable = false;
	private int activeClientVersion;

	{
		kryo = new Kryo();
		kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
		kryo.addDefaultSerializer(Arrays.asList().getClass(), ArraysListSerializer.class);
		kryo.addDefaultSerializer(Collections.unmodifiableList(new ArrayList<Object>()).getClass(), CollectionsSerializers.UnmodifiableListSerializer.class);
		kryo.addDefaultSerializer(Collections.unmodifiableList(new LinkedList<Object>()).getClass(), CollectionsSerializers.UnmodifiableListSerializer.class);

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
	public void login(CallHandler<Void> handler, String userName, String password, int clientVersion)
	{
		LoginCall newCall = new LoginCall();

		newCall.userName = userName;
		newCall.password = password;
		newCall.clientVersion = clientVersion;
		newCall.handler = handler;

		this.userName = userName;
		this.password = password;
		this.clientVersion = clientVersion;
		pendingCalls.add(newCall);
	}

	@SuppressWarnings("unchecked")
	@Override
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
			AuthenticationResult authenticationResult = invokeLogin(call.userName, call.password, call.clientVersion);

			if (authenticationResult != AuthenticationResult.Authenticated)
			{
				throw createAuthenticationException(authenticationResult, "loginFailed");
			}

			call.succeed(null, synchronizer);
		}
		catch (Exception ex)
		{
			call.fail(ex, synchronizer);
		}
	}

	protected AuthenticationResult invokeLogin(String userName, String password, int clientVersion) throws Exception
	{
		AuthenticationResult authenticationResult = internalCall(LOGIN_FUNCTION, new Object[] { userName, password, clientVersion });

		if (authenticationResult == AuthenticationResult.Authenticated)
		{
			this.activeUserName = userName;
			this.activePassword = password;
			this.activeClientVersion = clientVersion;

			this.state = ConnectionState.Authenticated;
		}

		return authenticationResult;
	}

	protected void invokeFunction(FunctionCall call)
	{
		try
		{
			if ((state != ConnectionState.Authenticated) && (userName != null) && (password != null))
			{
				AuthenticationResult authenticationResult = invokeLogin(userName, password, clientVersion);

				if (authenticationResult != AuthenticationResult.Authenticated)
				{
					throw createAuthenticationException(authenticationResult, "loginFailed");
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

				AuthenticationResult authenticationResult = invokeLogin(activeUserName, activePassword, activeClientVersion);

				if (authenticationResult != AuthenticationResult.Authenticated)
				{
					throw createAuthenticationException(authenticationResult, "loginRepeated");
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
			AuthenticationResult authenticationResult = invokeLogin(userName, password, clientVersion);

			if (authenticationResult != AuthenticationResult.Authenticated)
			{
				throw createAuthenticationException(authenticationResult, "loginFailed");
			}
		}

		try
		{
			@SuppressWarnings("unchecked")
			T result = internalCall(name, args);

			return result;
		}
		catch (AuthenticationMissmatchException ex)
		{
			assert activeUserName != null : "activeUserName is null";
			assert activePassword != null : "activePassword is null";

			AuthenticationResult authenticationResult = invokeLogin(activeUserName, activePassword, clientVersion);

			if (authenticationResult != AuthenticationResult.Authenticated)
			{
				throw createAuthenticationException(authenticationResult, "loginRepeated");
			}

			@SuppressWarnings("unchecked")
			T result = internalCall(name, args);

			return result;
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T internalCall(String name, Object[] args) throws Exception
	{
		int tryCount = 0;

		while (true)
		{
			HttpURLConnection connection = null;

			try
			{
				connection = prepareConnection();

				// Service-Namen und Parameter an den Server schreiben
				OutputStream outputStream;

				if (gzipAvailable)
				{
					connection.setRequestProperty(StasisUtils.CONTENT_ENCODING_KEY, StasisUtils.GZIP_ENCODING);
					outputStream = new GZIPOutputStream(connection.getOutputStream());
				}
				else
				{
					outputStream = connection.getOutputStream();
				}

				output.setOutputStream(outputStream);

				kryo.writeObject(output, name);
				kryo.writeObject(output, state == ConnectionState.Authenticated); // Dem Server mitteilen ob wir davon ausgehen, dass wir bereits authentifiziert sind
				kryo.writeObject(output, args != null ? args : new Object[0]);

				output.close();
				output.setOutputStream(null);

				// Antwort erwarten
				String contentType = connection.getContentType();

				if (!StasisConstants.CONTENT_TYPE.equals(contentType))
				{
					boolean handled = false;

					if (handshakeHandler != null)
					{
						handled = handshakeHandler.handleResponse(connection, this, ++tryCount);
					}

					if (!handled)
					{
						throw createException("mimeTypeMissmatch");
					}

					continue;
				}

				boolean gzipUsed = StasisUtils.isUsingGzipEncoding(connection.getHeaderField(StasisUtils.CONTENT_ENCODING_KEY));
				cookieManager.storeCookies(connection);

				// Antwort lesen
				InputStream inputStream = connection.getInputStream();

				if (gzipUsed)
				{
					gzipAvailable = true;
					inputStream = new GZIPInputStream(inputStream);
				}

				input.setInputStream(inputStream);

				// Antwort aus Datenstrom lesen
				Object[] result = kryo.readObject(input, Object[].class);

				if (result.length == 0)
				{
					return null;
				}

				if (result[0] instanceof Exception)
				{
					throw (Exception)result[0];
				}

				@SuppressWarnings("unchecked")
				T returnValue = (T)result[0];

				return returnValue;
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
	}

	protected HttpURLConnection prepareConnection() throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setRequestMethod(REQUEST_METHOD);
		connection.setRequestProperty(CONTENT_TYPE_KEY, StasisConstants.CONTENT_TYPE);
		connection.setRequestProperty(StasisUtils.ACCEPT_ENCODING_KEY, StasisUtils.GZIP_ENCODING);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		cookieManager.setCookies(connection);

		return connection;
	}

	protected StasisException createException(String id)
	{
		String message;

		try
		{
			message = resourceBundle.getString(id);
		}
		catch (MissingResourceException ex)
		{
			message = "#" + id;
		}

		return new StasisException(id, message);
	}

	protected StasisException createAuthenticationException(AuthenticationResult authenticationResult, String id)
	{
		if (authenticationResult == AuthenticationResult.VersionMissmatch)
		{
			id = "versionMissmatch";
		}

		String message = resourceBundle.getString(id);
		return new StasisException(id, message);
	}
}

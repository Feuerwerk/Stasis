package de.boxxit.statis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.boxxit.statis.serializer.LocalDateSerializer;
import de.boxxit.statis.serializer.LocalDateTimeSerializer;
import de.boxxit.statis.serializer.LocalTimeSerializer;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * User: Christian Fruth
 */
public class HttpRemoteConnection extends RemoteConnection
{
	private static final String CONTENT_TYPE_KEY = "Content-Type";
	private static final String CONTENT_TYPE_VALUE = "application/x-stasis";
	private static final String REQUEST_METHOD = "POST";
	private static final String LOGIN_FUNCTION = "login";

	protected static class MissingAuthenticationException extends Exception
	{
	}

	private Kryo kryo = new Kryo();
	private Output output = new Output(4096);
	private Input input = new Input(4096);
	private URL url;
	private Map<String, String> cookies = new TreeMap<>();
	private ResourceBundle resourceBundle = ResourceBundle.getBundle(HttpRemoteConnection.class.getPackage().getName() + ".errors");

	{
		kryo.register(LocalTime.class, new LocalTimeSerializer());
		kryo.register(LocalDate.class, new LocalDateSerializer());
		kryo.register(LocalDateTime.class, new LocalDateTimeSerializer());
	}

	public HttpRemoteConnection(URL url)
	{
		this.url = url;
		this.state = ConnectionState.Unconnected;

	}

	@Override
	public void login() throws IOException, AuthenticationException
	{
		if ((userName != null) && (password != null))
		{
			if (!invokeLogin(userName, password))
			{
				throw new AuthenticationException();
			}
		}
	}

	public <T> T invoke(Class<T> returnType, String name, Object... args) throws IOException
	{
		boolean loginAttempt = false;

		if ((state != ConnectionState.Authenticated) && (userName != null) && (password != null))
		{
			loginAttempt = true;
			boolean authenticated = invokeLogin(userName, password);

			if (!authenticated)
			{
				throw createException("loginFailed");
			}
		}

	    try
		{
			return invokeInternal(returnType, name, args);
		}
		catch (MissingAuthenticationException ex)
		{
			if (loginAttempt)
			{
				throw createException("loginRepeated");
			}

			if ((userName == null) || (password == null))
			{
				throw createException("corruptedCredentials");
			}

			boolean authenticated = invokeLogin(userName, password);

			if (authenticated)
			{
				try
				{
					return invokeInternal(returnType, name, args);
				}
				catch (MissingAuthenticationException ex2)
				{
					throw createException("loginRepeated");
				}
			}
			else
			{
				throw createException("loginFailed");
			}
		}
	}

	protected boolean invokeLogin(String userName, String password) throws IOException
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
			readCookies(connection);

			input.setInputStream(connection.getInputStream());

			boolean authenticated = kryo.readObject(input, boolean.class);
			state = authenticated ? ConnectionState.Authenticated : ConnectionState.Connected;

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

	protected <T> T invokeInternal(Class<T> returnType, String name, Object... args) throws IOException, MissingAuthenticationException
	{
	    HttpURLConnection connection = null;

	    try
		{
			connection = prepareConnection();

			// Service-Namen und Parameter an den Server schreiben
			output.setOutputStream(connection.getOutputStream());

			kryo.writeObject(output, name);
			kryo.writeObject(output, args);

			output.close();
			output.setOutputStream(null);

			// Rückgabewert in Empfang nehmen
			readCookies(connection);

			input.setInputStream(connection.getInputStream());

			boolean authenticated = kryo.readObject(input, boolean.class);

			if (state == ConnectionState.Unconnected)
			{
				state = ConnectionState.Connected;
			}

			if ((state == ConnectionState.Authenticated) && !authenticated)
			{
				throw new MissingAuthenticationException();
			}

			SerializableException exception = kryo.readObjectOrNull(input, SerializableException.class);

			if (exception != null)
			{
				throw exception;
			}

			if (void.class.equals(returnType))
			{
				return null;
			}

			Object[] result = kryo.readObject(input, Object[].class);

			if (result.length != 1)
			{
				throw createException("wrongResult");
			}

			@SuppressWarnings("unchecked")
			T singleResult = (T)result[0];

			return singleResult;
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

	protected void readCookies(HttpURLConnection connection)
	{
		String headerName;

		for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; ++i)
		{
			if (headerName.equals("Set-Cookie"))
			{
				String cookie = connection.getHeaderField(i);
				cookie = cookie.substring(0, cookie.indexOf(";"));
				int index = cookie.indexOf('=');
				String cookieName = cookie.substring(0, index);
				String cookieValue = cookie.substring(index + 1);

				cookies.put(cookieName, cookieValue);
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

		for (Map.Entry<String, String> cookieEntry : cookies.entrySet())
		{
			connection.setRequestProperty("Cookie", cookieEntry.getKey() + "=" + cookieEntry.getValue());
		}

		return connection;
	}

	protected StasisException createException(String id)
	{
		String message = resourceBundle.getString(id);
		return new StasisException(id, message);
	}
}

package de.boxxit.statis;

/**
 * User: Christian Fruth
 */
public class TestMich
{
	public TestMich()
	{
	}

	public static void main(String[] args) throws Exception
	{
		RemoteConnection connection = RemoteConnection.createConnection("http://localhost:8080/public/stasis");
		connection.setCredentials("christian", "christianPW");

		try
		{
			String result = connection.invoke(String.class, "test.mich", 42);

			System.out.println(result);

			connection.invoke(void.class, "test.wuff", 42);
		}
		catch (SerializableException ex)
		{
			System.err.println(ex.getId() + ": " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}

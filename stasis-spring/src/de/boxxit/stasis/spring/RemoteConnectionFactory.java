package de.boxxit.stasis.spring;

import com.esotericsoftware.kryo.Serializer;
import de.boxxit.stasis.RemoteConnection;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.URL;
import java.util.List;

/**
 * User: Christian Fruth
 */
public class RemoteConnectionFactory implements FactoryBean<RemoteConnection>, InitializingBean
{
	private RemoteConnection connection;
	private List<Registration> registeredSerializers;
	private URL endpointUrl;

	public RemoteConnectionFactory()
	{
	}

	public void setEndpointUrl(URL endpointUrl)
	{
		this.endpointUrl = endpointUrl;
	}

	public void setRegisteredSerializers(List<Registration> registeredSerializers)
	{
		this.registeredSerializers = registeredSerializers;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception
	{
		if (endpointUrl == null)
		{
			throw new IllegalArgumentException("property endpointUrl must not be null");
		}

		connection = RemoteConnection.createConnection(endpointUrl);

		if (connection == null)
		{
			throw new IllegalArgumentException("Stasis RemoteConnection couldn't be created with url '" + endpointUrl + "'");
		}

		if (registeredSerializers != null)
		{
			for (Registration registration : registeredSerializers)
			{
				Serializer serializer = registration.getSerializer();

				if ((serializer == null) && (registration.getSerializerClass() != null))
				{
					serializer = registration.getSerializerClass().newInstance();
				}

				Class type = (Class)registration.getType();

				if (registration.getId() == null)
				{
					assert (serializer != null);
					connection.register(type, serializer);
				}
				else if (serializer == null)
				{
					connection.register(type, registration.getId());
				}
				else
				{
					connection.register(type, serializer, registration.getId());
				}
			}
		}
	}

	@Override
	public RemoteConnection getObject() throws Exception
	{
		return connection;
	}

	@Override
	public Class<?> getObjectType()
	{
		return RemoteConnection.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}
}

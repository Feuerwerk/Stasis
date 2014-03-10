package de.boxxit.stasis.spring;

import java.net.URL;
import java.util.List;
import com.esotericsoftware.kryo.Serializer;
import de.boxxit.stasis.RemoteConnection;
import de.boxxit.stasis.RemoteConnectionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: Christian Fruth
 */
public class RemoteConnectionFactoryBean implements FactoryBean<RemoteConnection>, InitializingBean
{
	private RemoteConnection connection;
	private List<Registration> registeredSerializers;
	private URL endpointUrl;
	private Class<? extends Serializer<?>> defaultSerializer;

	public RemoteConnectionFactoryBean()
	{
	}

	public void setDefaultSerializer(Class<? extends Serializer<?>> defaultSerializer)
	{
		this.defaultSerializer = defaultSerializer;
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

		connection = RemoteConnectionFactory.createConnection(endpointUrl);

		if (connection == null)
		{
			throw new IllegalArgumentException("Stasis RemoteConnection couldn't be created with url '" + endpointUrl + "'");
		}

		if (registeredSerializers != null)
		{
			for (Registration registration : registeredSerializers)
			{
				Serializer<?> serializer = registration.getSerializer();

				if ((serializer == null) && (registration.getSerializerClass() != null))
				{
					serializer = registration.getSerializerClass().newInstance();
				}

				@SuppressWarnings("rawtypes")
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

				if (defaultSerializer != null)
				{
					connection.setDefaultSerializer(defaultSerializer);
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

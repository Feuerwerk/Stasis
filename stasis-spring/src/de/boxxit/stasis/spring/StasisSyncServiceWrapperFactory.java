package de.boxxit.stasis.spring;

import de.boxxit.statis.RemoteConnection;
import de.boxxit.statis.StasisSyncServiceWrapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created with IntelliJ IDEA.
 * User: sseibel
 * Date: 27.02.13
 * Time: 19:41
  */
public class StasisSyncServiceWrapperFactory implements FactoryBean<Object>, InitializingBean
{
	private Class<?> serviceInterface;
	private Object serviceProxy;
	private String serviceName;
	private RemoteConnection connection;

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public void setServiceInterface(Class<?> serviceInterface)
	{
		this.serviceInterface = serviceInterface;
	}

	public void setConnection(RemoteConnection connection)
	{
		this.connection = connection;
	}

	@Override
	public Object getObject() throws Exception
	{
		return serviceProxy;
	}

	@Override
	public Class<?> getObjectType()
	{
		return serviceInterface;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		serviceProxy = StasisSyncServiceWrapper.create(serviceInterface, connection, serviceName);
	}
}

package de.boxxit.statis.spring;

import java.net.MalformedURLException;
import de.boxxit.statis.RemoteConnection;
import de.boxxit.statis.ResultHandler;
import de.boxxit.statis.StasisAsyncServiceWrapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: Christian Fruth
 */
public class StasisAsyncServiceWrapperFactory implements FactoryBean<Object>, InitializingBean
{
	private Class<?> serviceInterface;
	private RemoteConnection connection;
	private Object serviceProxy;
	private String serviceName;
	private ResultHandler<Exception> defaultErrorHandler;

	public StasisAsyncServiceWrapperFactory()
	{
	}

	public Class<?> getServiceInterface()
	{
		return serviceInterface;
	}

	public void setServiceInterface(Class<?> serviceInterface)
	{
		this.serviceInterface = serviceInterface;
	}

	public void setConnection(RemoteConnection connection)
	{
		this.connection = connection;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public void setDefaultErrorHandler(ResultHandler<Exception> defaultErrorHandler)
	{
		this.defaultErrorHandler = defaultErrorHandler;
	}

	@Override
	public Object getObject()
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
	public void afterPropertiesSet() throws MalformedURLException
	{
		serviceProxy = StasisAsyncServiceWrapper.create(serviceInterface, connection, serviceName, defaultErrorHandler);
	}
}

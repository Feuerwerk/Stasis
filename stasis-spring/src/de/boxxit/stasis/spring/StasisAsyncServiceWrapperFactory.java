package de.boxxit.stasis.spring;

import java.net.MalformedURLException;
import de.boxxit.stasis.ErrorHandler;
import de.boxxit.stasis.RemoteConnection;
import de.boxxit.stasis.StasisAsyncServiceWrapper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * User: Christian Fruth
 */
public class StasisAsyncServiceWrapperFactory<T> implements FactoryBean<T>, InitializingBean
{
	private Class<T> serviceInterface;
	private RemoteConnection connection;
	private T serviceProxy;
	private String serviceName;
	private ErrorHandler defaultErrorHandler;

	public StasisAsyncServiceWrapperFactory()
	{
	}

	public Class<T> getServiceInterface()
	{
		return serviceInterface;
	}

	public void setServiceInterface(Class<T> serviceInterface)
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

	public void setDefaultErrorHandler(ErrorHandler defaultErrorHandler)
	{
		this.defaultErrorHandler = defaultErrorHandler;
	}

	@Override
	public T getObject()
	{
		return serviceProxy;
	}

	@Override
	public Class<T> getObjectType()
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

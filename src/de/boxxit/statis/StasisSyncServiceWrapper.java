package de.boxxit.statis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created with IntelliJ IDEA.
 * User: sseibel
 * Date: 27.02.13
 * Time: 19:41
 * To change this template use File | Settings | File Templates.
 */
public class StasisSyncServiceWrapper implements FactoryBean<Object>, InitializingBean {

    private class InvocationHandlerImpl implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            return remoteConnection.callSync(serviceName + "." + method.getName(), args);
        }
    }

    private Class<?> serviceInterface;
    private Object serviceProxy;
    private String serviceName;
    private RemoteConnection remoteConnection;

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void setRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    @Override
    public Object getObject() throws Exception {
        return serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serviceProxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {serviceInterface}, new InvocationHandlerImpl());
    }
}

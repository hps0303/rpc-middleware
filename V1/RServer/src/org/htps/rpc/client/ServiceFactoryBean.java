package org.htps.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.htps.rpc.bean.RPCConstant;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @description proxy the rpc client to send msg
 * @path org.htps.rpc.client.ServiceFactoryBean
 * @author heps
 * @date 2018年5月30日
 */
public class ServiceFactoryBean implements FactoryBean<Object>, InitializingBean{

	private Object proxyObj;
	private String serviceName;
	private String serviceAddr;
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceAddr(String serviceAddr) {
		this.serviceAddr = serviceAddr;
	}

	@Override
	public Object getObject() throws Exception {
		return this.proxyObj;
	}

	@Override
	public Class<?> getObjectType() {
		return this.proxyObj.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.proxyObj = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Class.forName(this.serviceName)}, new InvocationHandler(){

			@Override
			public Object invoke(Object o, Method m, Object[] args)
					throws Throwable {
				String method = m.getName();
				if ((method.equals("toString")) || (method.equals("hashCode")) || (method.equals("getClass")) || (method.equals("notify")) || (method.equals("notifyAll")) || (method.equals("wait")))
		        {
		          throw new NoSuchMethodError();
		        }
				String[] addrs = ServiceFactoryBean.this.serviceAddr.split(":");
				String host = addrs[0];
				int port = Integer.valueOf(addrs[1]);
				String clazz = ServiceFactoryBean.this.serviceName;
				String resClazz = m.getReturnType().getName();
				@SuppressWarnings("rawtypes")
				Class[] parameterTypes = m.getParameterTypes();
				int parameterTotal = parameterTypes.length;
				String[] argTypes = new String[parameterTotal];
				for(int i=0; i<parameterTotal; i++){
					argTypes[i] = parameterTypes[i].getName();
				}
				return RPCConstant.rpcRequst(host, port, method, clazz, resClazz, argTypes, args);
			}
		});
	}

}

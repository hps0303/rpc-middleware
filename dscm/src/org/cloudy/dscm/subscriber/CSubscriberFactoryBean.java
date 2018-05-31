package org.cloudy.dscm.subscriber;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class CSubscriberFactoryBean implements FactoryBean<Object>, InitializingBean {
	private String interfaceName;
	private String interfaceUrl;
	private Object proxyObj;
	private String sync = "sync";
	private String safe = "safe";

	public void setSync(String sync) {
		this.sync = sync;
	}

	public void setSafe(String safe) {
		this.safe = safe;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setInterfaceUrl(String interfaceUrl) {
		this.interfaceUrl = interfaceUrl;
	}

	public Object getObject() throws Exception {
		return this.proxyObj;
	}

	public Class<?> getObjectType() {
		return this.proxyObj == null ? Object.class : this.proxyObj.getClass();
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		this.proxyObj = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Class.forName(this.interfaceName) }, new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if ((method.getName().equals("toString")) || (method.getName().equals("hashCode")) || (method.getName().equals("getClass")) || (method.getName().equals("notify"))
						|| (method.getName().equals("notifyAll")) || (method.getName().equals("wait"))) {
					throw new NoSuchMethodError();
				}

				String uuid = UUID.randomUUID().toString();
				long currentTimestamp = System.currentTimeMillis();
				try {
					return CConnectionExecutorImpl.INST
							.execute(proxy, method, args, CSubscriberFactoryBean.this.interfaceUrl, CSubscriberFactoryBean.this.sync, CSubscriberFactoryBean.this.safe, uuid);
				} finally {
					CConnectionExecutorImpl.INST.waste(new StringBuffer(CSubscriberFactoryBean.this.interfaceUrl).append("?").append(method.getName()).append(" uuid=").append(uuid)
							.append(" all-client-time=").append(System.currentTimeMillis() - currentTimestamp));
				}
			}
		});
	}
}

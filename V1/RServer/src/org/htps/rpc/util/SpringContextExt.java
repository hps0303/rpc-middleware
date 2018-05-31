package org.htps.rpc.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("springContextExt")
@Lazy(false)
public final class SpringContextExt implements ApplicationContextAware, DisposableBean {

	/** applicationContext */
	private static ApplicationContext applicationContext;

	private SpringContextExt() {}

	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextExt.applicationContext = applicationContext;
	}

	public void destroy() throws Exception {
		applicationContext = null;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String name) {
		Assert.hasText(name);
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> type) {
		Assert.hasText(name);
		Assert.notNull(type);
		return applicationContext.getBean(name, type);
	}
	
	public static <T> T getBeanByClazz(Class<T> type) {
		Assert.notNull(type);
		String name = lowercaseCapitalLetter(type.getSimpleName());
		Assert.hasText(name);
		return applicationContext.getBean(name, type);
	}
	
	public static String lowercaseCapitalLetter(String name){
		String cap = name.substring(0,1);
		return new StringBuffer(cap.toLowerCase()).append(name.substring(1)).toString();
	}
}


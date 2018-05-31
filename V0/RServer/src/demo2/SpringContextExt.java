package demo2;

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

	/**
	 * 不可实例化
	 */
	private SpringContextExt() {}

	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextExt.applicationContext = applicationContext;
	}

	public void destroy() throws Exception {
		applicationContext = null;
	}

	/**
	 * 获取applicationContext
	 * 
	 * @return applicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 获取实例
	 * 
	 * @param name
	 *            Bean名称
	 * @return 实例
	 */
	public static Object getBean(String name) {
		Assert.hasText(name);
		return applicationContext.getBean(name);
	}

	/**
	 * 获取实例
	 * 
	 * @param name
	 *            Bean名称
	 * @param type
	 *            Bean类型
	 * @return 实例
	 */
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


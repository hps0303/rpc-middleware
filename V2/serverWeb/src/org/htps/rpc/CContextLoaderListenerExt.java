package org.htps.rpc;

import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.cloudy.dscm.publisher.CServer;
import org.springframework.web.context.ContextLoaderListener;

public class CContextLoaderListenerExt extends ContextLoaderListener implements org.cloudy.dscm.context.CContextLoaderListener {
	public void contextInitialized(ServletContextEvent event) {
		
		try {
			Properties props=new Properties();
			props.load(CContextLoaderListenerExt.class.getClassLoader().getResourceAsStream("dscm.properties"));
			CServer.getInstance(this).startHttpserver(props);
		} catch (Exception exc) {
			exc.printStackTrace();
			throw new RuntimeException(exc.getCause());
		}

		super.contextInitialized(event);
	}

	public void contextDestroyed(ServletContextEvent event) {
		CServer.getInstance(this).stopHttpserver();
		super.contextDestroyed(event);
	}

	@Override
	public Object classByName(String name) {
		return getCurrentWebApplicationContext().getBean(name);
	}
}

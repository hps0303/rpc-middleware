package org.cloudy.dscm.common;

import io.netty.util.internal.logging.InternalLogger;

public class CLogger {

	private InternalLogger logger = null;

	public void log(String str) {
		this.logger.info(str);
	}

	public void log(StringBuilder str) {
		this.logger.info(str.toString());
	}

	public void log(StringBuffer str) {
		this.logger.info(str.toString());
	}

	public CLogger(InternalLogger logger) {
		this.logger = logger;
	}
}
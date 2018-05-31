package org.cloudy.dscm.subscriber;

import java.lang.reflect.Method;

public abstract interface CConnectionExecutor {
	public abstract Object execute(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, String paramString1, String paramString2, String paramString3, String paramString4)
			throws Throwable;

	public abstract void waste(StringBuffer paramStringBuffer);
}

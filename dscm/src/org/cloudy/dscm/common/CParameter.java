package org.cloudy.dscm.common;

import java.util.List;

public class CParameter {

	private String clazz;
	private Object object;

	public Object object() {
		return this.object;
	}

	public void object(Object object) {
		this.object = object;
	}

	public String clazz() {
		return this.clazz;
	}

	public void clazz(String clazz) {
		this.clazz = clazz;
	}

	public static Class<?> parse(String types) throws ClassNotFoundException {
		Class<?> the = CConf.clazz(types);
		if (the == null) {
			the = Class.forName(types);
		}
		return the;
	}

	public static Class<?>[] parse(List<String> types) throws ClassNotFoundException {
		Class<?>[] clazz = new Class[types.size()];
		for (int i = 0; i < types.size(); i++) {
			clazz[i] = CConf.clazz((String) types.get(i));
			if (clazz[i] == null) {
				clazz[i] = Class.forName((String) types.get(i));
			}
		}
		return clazz;
	}
}

package org.htps.rpc.bean;

/**
 * @description rpc parameter bean, for serial
 * @path org.htps.rpc.bean.RPCBean
 * @author heps
 * @date 2018年5月30日
 */
public class RPCBean {

	private String clazz;
	private String method;
	private Object[] args;
	private String[] argTypes;
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public String[] getArgTypes() {
		return argTypes;
	}
	public void setArgTypes(String[] argTypes) {
		this.argTypes = argTypes;
	}
	
	
}

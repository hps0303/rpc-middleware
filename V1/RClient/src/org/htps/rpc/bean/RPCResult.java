package org.htps.rpc.bean;

/**
 * @description rpc result bean, for serial
 * @path org.htps.rpc.bean.RPCResult
 * @author heps
 * @date 2018年5月30日
 */
public class RPCResult {

	private String clazz;
	private Object o;
	private Exception e;
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public Object getO() {
		return o;
	}
	public void setO(Object o) {
		this.o = o;
	}
	public Exception getE() {
		return e;
	}
	public void setE(Exception e) {
		this.e = e;
	}
	
	
}

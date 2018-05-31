package demo2;

public class RPCBean {

	private String clazz;
	private String method;
	private Object[] args;
	private String[] argTypes;
	private String rsClazz;
	
	public String getRsClazz() {
		return rsClazz;
	}
	public void setRsClazz(String rsClazz) {
		this.rsClazz = rsClazz;
	}
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

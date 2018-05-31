package demo2.service;

import java.util.Date;

public class TestBean {

	private Long id;
	private String name;
	private Date createTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return new StringBuffer("id:").append(id).append(",name:").append(name).append(",createTime:").append(createTime).toString();
	}
	
	
}

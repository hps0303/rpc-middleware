package org.htps.rpc.bean;

import java.util.List;
import java.util.Map;

public class PageDataList <T>{
	
	/**
	 * 分页信息
	 */
	private Page page;
	
	/**
	 * 数据列表
	 */
	private List<T> list;
	
	/**
	 * 分页类型
	 */
	private int type;
	
	private Map<String,Object> countInfo;

	public PageDataList() {
		super();
	}

	public PageDataList(Page page, List<T> list) {
		super();
		this.page = page;
		this.list = list;
	}

	public PageDataList(Page page, List<T> list, Map<String, Object> countInfo) {
		super();
		this.page = page;
		this.list = list;
		this.countInfo = countInfo;
	}
	
	public PageDataList(Page page, List<T> list, int type) {
		super();
		this.page = page;
		this.list = list;
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Map<String,Object> getCountInfo() {
		return countInfo;
	}

	public void setCountInfo(Map<String,Object> countInfo) {
		this.countInfo = countInfo;
	}

}

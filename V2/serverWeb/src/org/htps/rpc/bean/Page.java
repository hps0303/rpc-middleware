package org.htps.rpc.bean;

public class Page {
	/**
	 * 记录总数
	 */
	private long total;

	/**
	 * 当前页码
	 */
	private Integer pageIndex;

	/**
	 * 起始位置
	 */
	private Integer startPos;
	/**
	 * 总页数
	 */
	private Integer pages;

	/**
	 * 每页数量
	 */
	private Integer pageSize;

	/**
	 * 开始游标
	 */
	private long start;

	/**
	 * 结束游标
	 */
	private long end;

	/**
	 * 默认每页数量
	 */
	public static Integer ROWS = 10;

	public Page() {
		super();
	}

	public Page(Integer total, Integer pageIndex) {
		this(total, pageIndex, ROWS);
	}

	public Page(long total, Integer pageIndex, Integer pageSize) {
		super();
		if (pageIndex == -1) {
			this.pageIndex = pageIndex;
		} else {
			this.pageIndex = pageIndex < -2 || 0 == pageIndex ? 1 : pageIndex;
		}
		this.total = total;
		this.pageSize = pageSize;
		this.pages = (int) Math.ceil((total + 0.0) / pageSize);
		count();
	}

	private void count() {
		if (this.pageIndex == -1 || this.pageIndex == -2) {
			this.start = this.pageIndex;
		} else {
			this.start = pageSize * (pageIndex - 1);
		}
		this.end = pageSize * (pageIndex);
		if (this.end > total) {
			this.end = total;
		}
	}

	/**
	 * 当前页是否有上一页
	 * 
	 * @return
	 */
	public boolean hasPreview() {
		if (pages > 1 && pageIndex > 1) {
			return true;
		}
		return false;
	}

	/**
	 * 当前页是否有下一页
	 * 
	 * @return
	 */
	public boolean hasNext() {
		if (pages > 1 && pageIndex < pages) {
			return true;
		}
		return false;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public Integer getStartPos() {
		if (getPageIndex() <= 0) {
			return 0;
		}
		startPos = (getPageIndex() - 1) * getPageSize();
		return startPos;
	}
}
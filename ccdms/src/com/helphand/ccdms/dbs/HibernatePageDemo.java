package com.helphand.ccdms.dbs;

import java.util.List;

public class HibernatePageDemo {
	private int page_number;
	private int page_size;
	private int total_page_count;
	private int total_record_count;
	private List<Object> list;

	public int getPage_number() {
		return page_number;
	}

	public void setPage_number(int page_number) {
		this.page_number = page_number;
	}

	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

	public int getTotal_page_count() {
		return total_page_count;
	}

	public void setTotal_page_count(int total_page_count) {
		this.total_page_count = total_page_count;
	}

	public int getTotal_record_count() {
		return total_record_count;
	}

	public void setTotal_record_count(int total_record_count) {
		this.total_record_count = total_record_count;
	}

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

}

package com.helphand.ccdms.beans;

public class conn_rate_bean {

	private String now_date;
	private String now_time;
	private int callin_num;
	private int conn_num;
	private String conn_rate;

	public String getNow_date() {
		return now_date;
	}

	public void setNow_date(String now_date) {
		this.now_date = now_date;
	}

	public String getNow_time() {
		return now_time;
	}

	public void setNow_time(String now_time) {
		this.now_time = now_time;
	}

	public int getCallin_num() {
		return callin_num;
	}

	public void setCallin_num(int callin_num) {
		this.callin_num = callin_num;
	}

	public int getConn_num() {
		return conn_num;
	}

	public void setConn_num(int conn_num) {
		this.conn_num = conn_num;
	}

	public String getConn_rate() {
		return conn_rate;
	}

	public void setConn_rate(String conn_rate) {
		this.conn_rate = conn_rate;
	}

}

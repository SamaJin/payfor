package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="plan_result")
@SuppressWarnings("serial")
public class plan_result implements Serializable {
	private int id;
	private String customer_name;
	private String call_result_time;

	@Id
	@Column(name="id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="customer_name")
	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	@Column(name="call_result_time")
	public String getCall_result_time() {
		return call_result_time;
	}

	public void setCall_result_time(String call_result_time) {
		this.call_result_time = call_result_time;
	}

}

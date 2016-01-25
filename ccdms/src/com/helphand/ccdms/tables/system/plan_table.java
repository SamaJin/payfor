package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "plan_table")
@SuppressWarnings("serial")
public class plan_table implements Serializable{
	private int id;
	private String customer_name;
	private int call_record_type;

	@Id
	@Column(name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "customer_name")
	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	@Column(name = "call_record_type")
	public int getCall_record_type() {
		return call_record_type;
	}

	public void setCall_record_type(int call_record_type) {
		this.call_record_type = call_record_type;
	}
}

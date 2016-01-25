package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="jx_ds")
@SuppressWarnings("serial")
public class jx_ds implements Serializable {

	private int number_segment;
	private String province_name;
	private String city_name;

	@Id
	@Column(name="number_segment")
	public int getNumber_segment() {
		return number_segment;
	}

	public void setNumber_segment(int number_segment) {
		this.number_segment = number_segment;
	}

	@Column(name="province_name")
	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}

	@Column(name="city_name")
	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

}

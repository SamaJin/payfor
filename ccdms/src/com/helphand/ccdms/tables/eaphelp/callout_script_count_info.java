package com.helphand.ccdms.tables.eaphelp;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "callout_script_count_info")
@SuppressWarnings("serial")
public class callout_script_count_info implements Serializable {

	private String called;
	private int script_id;
	private String script_name;
	private int count_option;
	private String describe;

	@Id
	@Column(name="called")
	public String getCalled() {
		return called;
	}

	public void setCalled(String called) {
		this.called = called;
	}

	@Column(name="script_id")
	public int getScript_id() {
		return script_id;
	}

	public void setScript_id(int script_id) {
		this.script_id = script_id;
	}

	@Column(name="script_name")
	public String getScript_name() {
		return script_name;
	}

	public void setScript_name(String script_name) {
		this.script_name = script_name;
	}

	@Column(name="count_option")
	public int getCount_option() {
		return count_option;
	}

	public void setCount_option(int count_option) {
		this.count_option = count_option;
	}

	@Column(name="describe")
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

}

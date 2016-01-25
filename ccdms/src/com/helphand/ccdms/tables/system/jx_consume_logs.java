package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jx_consume_logs")
@SuppressWarnings("serial")
public class jx_consume_logs implements Serializable {

	private int id;
	private String ring_id;
	private String ring_name;
	private String singer_name;
	private String ring_price;
	private String agent_id;
	private String user_mobile;
	private String ring_type;
	private String cp_name;
	private String action_type;
	private String operate_time;

	public jx_consume_logs() {

	}

	@Id
	@Column(name="id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="ring_id")
	public String getRing_id() {
		return ring_id;
	}

	public void setRing_id(String ring_id) {
		this.ring_id = ring_id;
	}

	@Column(name="ring_name")
	public String getRing_name() {
		return ring_name;
	}

	public void setRing_name(String ring_name) {
		this.ring_name = ring_name;
	}

	@Column(name="singer_name")
	public String getSinger_name() {
		return singer_name;
	}

	public void setSinger_name(String singer_name) {
		this.singer_name = singer_name;
	}

	@Column(name="ring_price")
	public String getRing_price() {
		return ring_price;
	}

	public void setRing_price(String ring_price) {
		this.ring_price = ring_price;
	}

	@Column(name="agent_id")
	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	@Column(name="user_mobile")
	public String getUser_mobile() {
		return user_mobile;
	}

	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}

	@Column(name="ring_type")
	public String getRing_type() {
		return ring_type;
	}

	public void setRing_type(String ring_type) {
		this.ring_type = ring_type;
	}

	@Column(name="cp_name")
	public String getCp_name() {
		return cp_name;
	}

	public void setCp_name(String cp_name) {
		this.cp_name = cp_name;
	}

	@Column(name="action_type")
	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}

	@Column(name="operate_time")
	public String getOperate_time() {
		return operate_time;
	}

	public void setOperate_time(String operate_time) {
		this.operate_time = operate_time;
	}

}

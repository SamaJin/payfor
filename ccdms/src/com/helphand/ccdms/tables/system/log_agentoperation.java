package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.helphand.ccdms.beans.log_agentoperation_id;

@Entity
@Table(name = "log_agentoperation")
@IdClass(log_agentoperation_id.class)
@SuppressWarnings("serial")
public class log_agentoperation implements Serializable {

	private String timestamp;
	private String agentid;
	private String operation;
	private String time1;
	private String time2;
 
	@Id
	@Column(name="timestamp")
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name="agentid")
	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	@Column(name="operation")
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Id
	@Column(name="time1")
	public String getTime1() {
		return time1;
	}

	public void setTime1(String time1) {
		this.time1 = time1;
	}

	@Column(name="time2")
	public String getTime2() {
		return time2;
	}

	public void setTime2(String time2) {
		this.time2 = time2;
	}

}

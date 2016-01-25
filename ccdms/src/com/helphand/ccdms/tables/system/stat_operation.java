package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name = "stat_operation")
public class stat_operation  implements Serializable {

	
	private String timestamp;
	
	private String agentid;
	
	private String operation;
	
	private String time1;
	
	private String time2;
	
	private String agentmode;
	
	private String operationtime;
	
	
	
	
	@Column(name = "operationtime")
	public String getOperationtime() {
		return operationtime;
	}

	public void setOperationtime(String operationtime) {
		this.operationtime = operationtime;
	}

	@Column(name = "dtime")
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	@Id
	@Column(name = "agentid")
	public String getAgentid() {
		return agentid;
	}
	
	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}
	
	@Column(name = "operationflag")
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@Column(name = "begintime")
	public String getTime1() {
		return time1;
	}
	
	public void setTime1(String time1) {
		this.time1 = time1;
	}
	
	@Column(name = "endtime")
	public String getTime2() {
		return time2;
	}
	
	public void setTime2(String time2) {
		this.time2 = time2;
	}
	
	@Column(name = "reserve")
	public String getAgentmode() {
		return agentmode;
	}
	
	public void setAgentmode(String agentmode) {
		this.agentmode = agentmode;
	}
	
	 
}
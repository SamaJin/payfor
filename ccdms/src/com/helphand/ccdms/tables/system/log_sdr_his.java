package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.helphand.ccdms.beans.log_sdr_id;

@Entity
@Table(name="log_sdr_his")
@IdClass(log_sdr_id.class)
@SuppressWarnings("serial")
public class log_sdr_his implements Serializable {

	private String stimestamp;
	private String agentid;
	private String callerumber;
	private String callednumber;
	private String origcaller;
	private String origcalled;
	private String occupybegin;
	private String answertime;
	private String worknotready;
	private String workready;
	private String occupyend;
	private String createtime;

	public log_sdr_his() {

	}

	@Id
	@Column(name = "timestamp")
	public String getStimestamp() {
		return stimestamp;
	}

	public void setStimestamp(String stimestamp) {
		this.stimestamp = stimestamp;
	}

	@Column(name = "agentid")
	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	@Column(name = "callerumber")
	public String getCallerumber() {
		return callerumber;
	}

	public void setCallerumber(String callerumber) {
		this.callerumber = callerumber;
	}

	@Column(name = "callednumber")
	public String getCallednumber() {
		return callednumber;
	}

	public void setCallednumber(String callednumber) {
		this.callednumber = callednumber;
	}

	@Column(name = "origcaller")
	public String getOrigcaller() {
		return origcaller;
	}

	public void setOrigcaller(String origcaller) {
		this.origcaller = origcaller;
	}

	@Column(name = "origcalled")
	public String getOrigcalled() {
		return origcalled;
	}

	public void setOrigcalled(String origcalled) {
		this.origcalled = origcalled;
	}

	@Column(name = "occupybegin")
	public String getOccupybegin() {
		return occupybegin;
	}

	public void setOccupybegin(String occupybegin) {
		this.occupybegin = occupybegin;
	}

	@Id
	@Column(name = "answertime")
	public String getAnswertime() {
		return answertime;
	}

	public void setAnswertime(String answertime) {
		this.answertime = answertime;
	}

	@Column(name = "worknotready")
	public String getWorknotready() {
		return worknotready;
	}

	public void setWorknotready(String worknotready) {
		this.worknotready = worknotready;
	}

	@Column(name = "workready")
	public String getWorkready() {
		return workready;
	}

	public void setWorkready(String workready) {
		this.workready = workready;
	}

	@Column(name="occupyend")
	public String getOccupyend() {
		return occupyend;
	}

	public void setOccupyend(String occupyend) {
		this.occupyend = occupyend;
	}

	@Column(name="createtime")
	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	
}

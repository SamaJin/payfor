package com.helphand.ccdms.tables.eaphelp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "callout_result_count_info")
@SuppressWarnings("serial")
public class callout_result_count_info {
	
	private String count_id;//由script_id,agent_id组成中间用_连接
	private String callout_time;//时间
	private String agent_id;
	private String script_id;
	private String callNum;//外呼量
	private String openNum;//开通量
	private String opens;//开通率
	private String username;
	private String teamname;
	
	public callout_result_count_info() {
	}
	
	@Id
	@Column(name="count_id")
	public String getCount_id() {
		return count_id;
	}
	public void setCount_id(String count_id) {
		this.count_id = count_id;
	}
	
	@Column(name="callout_time")
	public String getCallout_time() {
		return callout_time;
	}
	public void setCallout_time(String callout_time) {
		this.callout_time = callout_time;
	}
	
	@Column(name="agent_id")
	public String getAgent_id() {
		return agent_id;
	}
	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}
	
	@Column(name="script_id")
	public String getScript_id() {
		return script_id;
	}
	public void setScript_id(String script_id) {
		this.script_id = script_id;
	}
	
	@Column(name="callNum")
	public String getCallNum() {
		return callNum;
	}
	public void setCallNum(String callNum) {
		this.callNum = callNum;
	}
	
	@Column(name="openNum")
	public String getOpenNum() {
		return openNum;
	}
	public void setOpenNum(String openNum) {
		this.openNum = openNum;
	}
	
	@Column(name="opens")
	public String getOpens() {
		return opens;
	}
	public void setOpens(String opens) {
		this.opens = opens;
	}
	
	@Column(name="username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name="teamname")
	public String getTeamname() {
		return teamname;
	}
	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}
}

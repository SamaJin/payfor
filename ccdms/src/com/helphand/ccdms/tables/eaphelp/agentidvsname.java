package com.helphand.ccdms.tables.eaphelp;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 员工表
 */
@Entity
@Table(name = "agentidvsname")
@SuppressWarnings("serial")
public class agentidvsname implements Serializable {
	private String agentid;// 工号
	private String username;// 员工名
	private String teamname;// 团队名

	public agentidvsname() {

	}

	@Id
	@Column(name = "agentid")
	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	@Column(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "teamname")
	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}
}

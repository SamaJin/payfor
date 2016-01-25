package com.helphand.ccdms.tables.eaphelp;

import java.io.*;

import javax.persistence.*;

/**
 * 用于报表显示pojo
 * 
 * @author lm
 *
 */
@SuppressWarnings("serial")
public class callout_rdlc implements Serializable {
	private String agent_id;// 员工工号
	private String script_name;// 脚本ID
	private String whtotal;// 脚本名称
	private String kttotal;// 外呼号码

	 

	public callout_rdlc() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the agent_id
	 */
	public String getAgent_id() {
		return agent_id;
	}

	/**
	 * @param agent_id the agent_id to set
	 */
	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	/**
	 * @return the script_name
	 */
	public String getScript_name() {
		return script_name;
	}

	/**
	 * @param script_name the script_name to set
	 */
	public void setScript_name(String script_name) {
		this.script_name = script_name;
	}

	public String getWhtotal() {
		return whtotal;
	}

	public void setWhtotal(String whtotal) {
		this.whtotal = whtotal;
	}

	public String getKttotal() {
		return kttotal;
	}

	public void setKttotal(String kttotal) {
		this.kttotal = kttotal;
	}

}

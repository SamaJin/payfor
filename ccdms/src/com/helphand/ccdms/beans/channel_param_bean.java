package com.helphand.ccdms.beans;

/**
 * 通道参数管理类
 */
public class channel_param_bean {

	private String agent_id;
	private String origcaller;
	private String origcalled;
	private String caller;
	private String called;

	public channel_param_bean() {
		
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getOrigcaller() {
		return origcaller;
	}

	public void setOrigcaller(String origcaller) {
		this.origcaller = origcaller;
	}

	public String getOrigcalled() {
		return origcalled;
	}

	public void setOrigcalled(String origcalled) {
		this.origcalled = origcalled;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCalled() {
		return called;
	}

	public void setCalled(String called) {
		this.called = called;
	}
}

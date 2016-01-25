package com.helphand.ccdms.tables.eaphelp;

import java.io.*;
import javax.persistence.*;

@Entity
@Table(name = "callout_result")
@SuppressWarnings("serial")
public class callout_result implements Serializable {
	private int id;
	private int script_id;//脚本ID
	private String script_name;//脚本名称
	private String callout_phone;// 外呼号码
	private String agent_id;// 外呼工号
	private String callout_time;// 外呼时间
	private String result_describe;// 外呼结果描述

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_callout_result", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_callout_result", sequenceName = "seq_callout_result")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "agent_id")
	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	@Column(name = "callout_time")
	public String getCallout_time() {
		return callout_time;
	}

	public void setCallout_time(String callout_time) {
		this.callout_time = callout_time;
	}

	@Column(name = "result_describe", length = 4000)
	public String getResult_describe() {
		return result_describe;
	}

	public void setResult_describe(String result_describe) {
		this.result_describe = result_describe;
	}

	@Column(name = "callout_phone")
	public String getCallout_phone() {
		return callout_phone;
	}

	public void setCallout_phone(String callout_phone) {
		this.callout_phone = callout_phone;
	}

	@Column(name = "script_id")
	public int getScript_id() {
		return script_id;
	}

	public void setScript_id(int script_id) {
		this.script_id = script_id;
	}

	@Column(name = "script_name")
	public String getScript_name() {
		return script_name;
	}

	public void setScript_name(String script_name) {
		this.script_name = script_name;
	}

}

package com.helphand.ccdms.tables.eaphelp;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "callout_channel")
@SuppressWarnings("serial")
public class callout_channel implements Serializable {

	private int id;                        //主键
	private int script_id;                 //脚本ID
	private String channel_name;           //通道名称
	private String origcaller;             //源主叫
	private String caller;                 //主叫
	private String origcalled;             //源被叫
	private String called;                 //被叫
	private int issue;                     //发布
	private int channel_status;            //激活、暂停
	private String create_time;            //创建时间
	
	public callout_channel() {
		
	}

	@Id
	@Column(name="id")
	@GeneratedValue(generator = "seq_callout_channel", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_callout_channel", sequenceName = "seq_callout_channel")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="script_id")
	public int getScript_id() {
		return script_id;
	}

	public void setScript_id(int script_id) {
		this.script_id = script_id;
	}

	@Column(name="channel_name")
	public String getChannel_name() {
		return channel_name;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

	@Column(name="origcaller")
	public String getOrigcaller() {
		return origcaller;
	}

	public void setOrigcaller(String origcaller) {
		this.origcaller = origcaller;
	}

	@Column(name="caller")
	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	@Column(name="origcalled")
	public String getOrigcalled() {
		return origcalled;
	}

	public void setOrigcalled(String origcalled) {
		this.origcalled = origcalled;
	}

	@Column(name="called")
	public String getCalled() {
		return called;
	}

	public void setCalled(String called) {
		this.called = called;
	}

	@Column(name="create_time")
	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	@Column(name="issue")
	public int getIssue() {
		return issue;
	}

	public void setIssue(int issue) {
		this.issue = issue;
	}

	@Column(name="channel_status")
	public int getChannel_status() {
		return channel_status;
	}

	public void setChannel_status(int channel_status) {
		this.channel_status = channel_status;
	}
}

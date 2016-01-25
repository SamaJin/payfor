package com.helphand.ccdms.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 质检管理
 */
@Entity
@Table(name="recording_check_result")
@SuppressWarnings("serial")
public class recording_new_check_result implements Serializable {
	private int id;
	private long recording_id;            //录音ID
	private String file_name;            //录音文件路径
	private String agent_id;             //工号
	private String recording_time;       //录音时间
	private String origcaller;           //源主叫
	private String origcalled;           //源被叫
	private String caller;               //主叫
	private String called;               //被叫
	private String check_time;           //抽检时间
	private String recording_type;       //录音类型
	private String call_type;            //业务范围
	private String call_leaf_type;       //业务类型
	private String error_type;           //差错类型
	private String check_type;           //考核类别
	private int check_result;            //评分
	private String result_describe;      //评语
	private String recheck_result;       //复检
	
	private int check_user_id;           //质检用户ID
	private int recheck_user_id;         //复检用户ID
	private String check_user_name;      //质检用户名称
	private String recheck_user_name;    //复检用户名称
	

	public recording_new_check_result() {

	}

	@Id
	@Column(name="id")
	@GeneratedValue(generator = "seq_recording_check_result", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_recording_check_result", sequenceName = "seq_1013")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="agent_id")
	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}
	
	@Column(name="recording_time")
	public String getRecording_time() {
		return recording_time;
	}

	public void setRecording_time(String recording_time) {
		this.recording_time = recording_time;
	}
	
	@Column(name="origcaller")
	public String getOrigcaller() {
		return origcaller;
	}

	public void setOrigcaller(String origcaller) {
		this.origcaller = origcaller;
	}
	
	@Column(name="origcalled")
	public String getOrigcalled() {
		return origcalled;
	}

	public void setOrigcalled(String origcalled) {
		this.origcalled = origcalled;
	}
	
	@Column(name="caller")
	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}
	
	@Column(name="called")
	public String getCalled() {
		return called;
	}

	public void setCalled(String called) {
		this.called = called;
	}
	
	@Column(name="check_time")
	public String getCheck_time() {
		return check_time;
	}

	public void setCheck_time(String check_time) {
		this.check_time = check_time;
	}
	
	@Column(name="recording_type")
	public String getRecording_type() {
		return recording_type;
	}

	public void setRecording_type(String recording_type) {
		this.recording_type = recording_type;
	}
	
	@Column(name="error_type")
	public String getError_type() {
		return error_type;
	}

	public void setError_type(String error_type) {
		this.error_type = error_type;
	}
	
	@Column(name="check_type")
	public String getCheck_type() {
		return check_type;
	}

	public void setCheck_type(String check_type) {
		this.check_type = check_type;
	}
	
	@Column(name="check_result")
	public int getCheck_result() {
		return check_result;
	}

	public void setCheck_result(int check_result) {
		this.check_result = check_result;
	}
	
	@Column(name="result_describe")
	public String getResult_describe() {
		return result_describe;
	}

	public void setResult_describe(String result_describe) {
		this.result_describe = result_describe;
	}
 
	
	@Column(name="recording_id")
	public long getRecording_id() {
		return recording_id;
	}

	public void setRecording_id(long recording_id) {
		this.recording_id = recording_id;
	}

	@Column(name="call_type")
	public String getCall_type() {
		return call_type;
	}

	public void setCall_type(String call_type) {
		this.call_type = call_type;
	}

	@Column(name="check_user_id")
	public int getCheck_user_id() {
		return check_user_id;
	}

	public void setCheck_user_id(int check_user_id) {
		this.check_user_id = check_user_id;
	}

	@Column(name="recheck_user_id")
	public int getRecheck_user_id() {
		return recheck_user_id;
	}

	public void setRecheck_user_id(int recheck_user_id) {
		this.recheck_user_id = recheck_user_id;
	}
	
	@Column(name="recheck_result")
	public String getRecheck_result() {
		return recheck_result;
	}

	public void setRecheck_result(String recheck_result) {
		this.recheck_result = recheck_result;
	}
	
	@Column(name="call_leaf_type")
	public String getCall_leaf_type() {
		return call_leaf_type;
	}

	public void setCall_leaf_type(String call_leaf_type) {
		this.call_leaf_type = call_leaf_type;
	}
	
	@Column(name="file_name")
	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	@Transient
	public String getCheck_user_name() {
		return check_user_name;
	}

	public void setCheck_user_name(String check_user_name) {
		this.check_user_name = check_user_name;
	}

	@Transient
	public String getRecheck_user_name() {
		return recheck_user_name;
	}

	public void setRecheck_user_name(String recheck_user_name) {
		this.recheck_user_name = recheck_user_name;
	}
	
}

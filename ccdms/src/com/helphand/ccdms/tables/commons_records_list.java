package com.helphand.ccdms.tables;

import java.io.Serializable;
import javax.persistence.*;

//录音记录表
@Entity
@SuppressWarnings("serial")
@Table(name = "commons_records_list")
public class commons_records_list implements Serializable {
	private int id;
	private String caller_number;
	private String orig_caller_number;
	private String called_number;
	private String orig_called_number;
	private String file_name;
	private String agent_code;
	private String start_time;
	private String end_time;
	private String is_check;
	private String check_user_id;
	
	private String time_length;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_commons_records_list", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_commons_records_list", sequenceName = "seq_1005")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "caller_number")
	public String getCaller_number() {
		return caller_number;
	}

	public void setCaller_number(String caller_number) {
		this.caller_number = caller_number;
	}

	@Column(name = "orig_caller_number")
	public String getOrig_caller_number() {
		return orig_caller_number;
	}

	public void setOrig_caller_number(String orig_caller_number) {
		this.orig_caller_number = orig_caller_number;
	}

	@Column(name = "called_number")
	public String getCalled_number() {
		return called_number;
	}

	public void setCalled_number(String called_number) {
		this.called_number = called_number;
	}

	@Column(name = "orig_called_number")
	public String getOrig_called_number() {
		return orig_called_number;
	}

	public void setOrig_called_number(String orig_called_number) {
		this.orig_called_number = orig_called_number;
	}

	@Column(name = "file_name")
	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	@Column(name = "agent_code")
	public String getAgent_code() {
		return agent_code;
	}

	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}

	@Column(name = "start_time")
	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	@Column(name = "end_time")
	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	@Column(name = "is_check")
	public String getIs_check() {
		return is_check;
	}

	public void setIs_check(String is_check) {
		this.is_check = is_check;
	}

	@Column(name = "check_user_id")
	public String getCheck_user_id() {
		return check_user_id;
	}

	public void setCheck_user_id(String check_user_id) {
		this.check_user_id = check_user_id;
	}

	@Transient
	public String getTime_length() {
		return time_length;
	}

	public void setTime_length(String time_length) {
		this.time_length = time_length;
	}
	
	
}

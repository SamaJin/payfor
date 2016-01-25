package com.helphand.ccdms.tables;

import java.io.Serializable;

import javax.persistence.*;

//呼入业务彩铃操作记录
@Entity
@SuppressWarnings("serial")
@Table(name = "commons_callin_order_logs")
public class commons_callin_order_logs implements Serializable {
	private int id;
	private String area_code;//区域代码
	private String user_number;//用戶號碼
	private String agent_code;//工號
	private String operate_time;//操作時間
	private String operate_type;//操作類型
	private String operate_result;//操作結果
	private String remarks;//备注

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_commons_callin_order_logs", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_commons_callin_order_logs", sequenceName = "seq_1010")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "area_code")
	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	@Column(name = "user_number")
	public String getUser_number() {
		return user_number;
	}

	public void setUser_number(String user_number) {
		this.user_number = user_number;
	}

	@Column(name = "agent_code")
	public String getAgent_code() {
		return agent_code;
	}

	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}

	@Column(name = "operate_time")
	public String getOperate_time() {
		return operate_time;
	}

	public void setOperate_time(String operate_time) {
		this.operate_time = operate_time;
	}

	@Column(name = "operate_type")
	public String getOperate_type() {
		return operate_type;
	}

	public void setOperate_type(String operate_type) {
		this.operate_type = operate_type;
	}

	@Column(name = "operate_result")
	public String getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}

	@Column(name = "remarks")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}

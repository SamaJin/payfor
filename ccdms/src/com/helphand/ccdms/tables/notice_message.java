package com.helphand.ccdms.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="notice_message")
@SuppressWarnings("serial")
public class notice_message implements Serializable {

	private int id;
	private String notice_title;          //消息标题
	private String notice_content;        //消息内容
	private String notice_from;           //消息创建用户
	private String notice_to;             //消息接收用户
	private String notice_status;         //消息状态
	private String notice_create_time;    //消息创建时间
	
	public notice_message() {
		
	}

	@Id
	@GeneratedValue(generator = "seq_notice_message", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_notice_message", sequenceName = "seq_1012")
	@Column(name="id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="notice_title")
	public String getNotice_title() {
		return notice_title;
	}

	public void setNotice_title(String notice_title) {
		this.notice_title = notice_title;
	}
	
	@Column(name="notice_content")
	public String getNotice_content() {
		return notice_content;
	}

	public void setNotice_content(String notice_content) {
		this.notice_content = notice_content;
	}

	@Column(name="notice_from")
	public String getNotice_from() {
		return notice_from;
	}

	public void setNotice_from(String notice_from) {
		this.notice_from = notice_from;
	}

	@Column(name="notice_to")
	public String getNotice_to() {
		return notice_to;
	}

	public void setNotice_to(String notice_to) {
		this.notice_to = notice_to;
	}

	@Column(name="notice_status")
	public String getNotice_status() {
		return notice_status;
	}

	public void setNotice_status(String notice_status) {
		this.notice_status = notice_status;
	}
	
	@Column(name="notice_create_time")
	public String getNotice_create_time() {
		return notice_create_time;
	}

	public void setNotice_create_time(String notice_create_time) {
		this.notice_create_time = notice_create_time;
	}

}

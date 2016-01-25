package com.helphand.ccdms.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="nitoce_user")
@SuppressWarnings("serial")
public class nitoce_user implements Serializable {
	private int id;
	private String user_name;
	private String user_password;
	private String user_status;

	public nitoce_user() {

	}

	@Id
	@SequenceGenerator(name = "seq_notice_message", sequenceName = "seq_1012")
	@Column(name="id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name="user_name")
	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	@Column(name="user_password")
	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}

	@Column(name="user_status")
	public String getUser_status() {
		return user_status;
	}

	public void setUser_status(String user_status) {
		this.user_status = user_status;
	}

}

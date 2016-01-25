package com.helphand.ccdms.tables;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "admin_user")
@SuppressWarnings("serial")
public class admin_user implements Serializable {
	private int id;
	private String admin_user_name;
	private String admin_user_password;
	private String admin_user_status;
	private String admin_user_type;
	private String admin_user_remarks;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_admin_user", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_admin_user", sequenceName = "seq_1000")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "admin_user_name")
	public String getAdmin_user_name() {
		return admin_user_name;
	}

	public void setAdmin_user_name(String admin_user_name) {
		this.admin_user_name = admin_user_name;
	}

	@Column(name = "admin_user_password")
	public String getAdmin_user_password() {
		return admin_user_password;
	}

	public void setAdmin_user_password(String admin_user_password) {
		this.admin_user_password = admin_user_password;
	}

	@Column(name = "admin_user_status")
	public String getAdmin_user_status() {
		return admin_user_status;
	}

	public void setAdmin_user_status(String admin_user_status) {
		this.admin_user_status = admin_user_status;
	}

	@Column(name = "admin_user_type")
	public String getAdmin_user_type() {
		return admin_user_type;
	}

	public void setAdmin_user_type(String admin_user_type) {
		this.admin_user_type = admin_user_type;
	}

	@Column(name = "admin_user_remarks")
	public String getAdmin_user_remarks() {
		return admin_user_remarks;
	}

	public void setAdmin_user_remarks(String admin_user_remarks) {
		this.admin_user_remarks = admin_user_remarks;
	}

}

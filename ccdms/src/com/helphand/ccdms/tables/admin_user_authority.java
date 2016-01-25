package com.helphand.ccdms.tables;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "admin_user_authority")
@SuppressWarnings("serial")
public class admin_user_authority implements Serializable {
	private int id;
	private int admin_user_id;
	private int admin_module_group_id;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_admin_user_authority", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_admin_user_authority", sequenceName = "seq_1001")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "admin_user_id")
	public int getAdmin_user_id() {
		return admin_user_id;
	}

	public void setAdmin_user_id(int admin_user_id) {
		this.admin_user_id = admin_user_id;
	}

	@Column(name = "admin_module_group_id")
	public int getAdmin_module_group_id() {
		return admin_module_group_id;
	}

	public void setAdmin_module_group_id(int admin_module_group_id) {
		this.admin_module_group_id = admin_module_group_id;
	}

}

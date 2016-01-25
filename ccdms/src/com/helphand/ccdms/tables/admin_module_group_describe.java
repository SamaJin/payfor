package com.helphand.ccdms.tables;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "admin_module_group_describe")
@SuppressWarnings("serial")
public class admin_module_group_describe implements Serializable {
	private int id;
	private int admin_module_group_id;
	private int admin_module_id;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_admin_module_group_describe", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_admin_module_group_describe", sequenceName = "seq_1004")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "admin_module_group_id")
	public int getAdmin_module_group_id() {
		return admin_module_group_id;
	}

	public void setAdmin_module_group_id(int admin_module_group_id) {
		this.admin_module_group_id = admin_module_group_id;
	}

	@Column(name = "admin_module_id")
	public int getAdmin_module_id() {
		return admin_module_id;
	}

	public void setAdmin_module_id(int admin_module_id) {
		this.admin_module_id = admin_module_id;
	}

}

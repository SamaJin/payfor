package com.helphand.ccdms.tables;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "admin_module_group")
@SuppressWarnings("serial")
public class admin_module_group implements Serializable {
	private int id;
	private String module_group_name;
	private String module_group_describe;
	private int module_status;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_admin_module_group", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_admin_module_group", sequenceName = "seq_1003")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "module_group_name")
	public String getModule_group_name() {
		return module_group_name;
	}

	public void setModule_group_name(String module_group_name) {
		this.module_group_name = module_group_name;
	}

	@Column(name = "module_group_describe")
	public String getModule_group_describe() {
		return module_group_describe;
	}

	public void setModule_group_describe(String module_group_describe) {
		this.module_group_describe = module_group_describe;
	}

	@Transient
	public int getModule_status() {
		return module_status;
	}

	public void setModule_status(int module_status) {
		this.module_status = module_status;
	}
}

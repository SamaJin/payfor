package com.helphand.ccdms.tables;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "admin_module")
@SuppressWarnings("serial")
public class admin_module implements Serializable {
	private int id;
	private int parent_id;
	private String module_name;
	private String module_uri;
	private String module_type;
	private String module_describe;
	private int module_status;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_admin_module", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_admin_module", sequenceName = "seq_1002")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "parent_id")
	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	@Column(name = "module_name")
	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	@Column(name = "module_type")
	public String getModule_type() {
		return module_type;
	}

	public void setModule_type(String module_type) {
		this.module_type = module_type;
	}

	@Column(name = "module_uri")
	public String getModule_uri() {
		return module_uri;
	}

	public void setModule_uri(String module_uri) {
		this.module_uri = module_uri;
	}

	@Column(name = "module_describe")
	public String getModule_describe() {
		return module_describe;
	}

	public void setModule_describe(String module_describe) {
		this.module_describe = module_describe;
	}

	@Transient
	public int getModule_status() {
		return module_status;
	}

	public void setModule_status(int module_status) {
		this.module_status = module_status;
	}
}

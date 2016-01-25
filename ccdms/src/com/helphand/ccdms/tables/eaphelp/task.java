package com.helphand.ccdms.tables.eaphelp;

import java.io.*;
import javax.persistence.*;

@Entity
@Table(name = "task")
@SuppressWarnings("serial")
public class task implements Serializable {
	private int id;
	private String name;// 报表名称
	private String create_time;// 创建时间
	private String type;

	

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_task", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_task", sequenceName = "seq_task")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "create_time")
	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

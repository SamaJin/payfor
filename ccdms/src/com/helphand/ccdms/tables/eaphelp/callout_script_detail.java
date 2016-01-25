package com.helphand.ccdms.tables.eaphelp;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "callout_script_detail")
@SuppressWarnings("serial")
public class callout_script_detail implements Serializable {
	private int id;
	private int script_id;
	private int content_sort;
	private String content_type;
	private String content_detail;

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_callout_script_detail", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_callout_script_detail", sequenceName = "seq_callout_script_detail")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "script_id")
	public int getScript_id() {
		return script_id;
	}

	public void setScript_id(int script_id) {
		this.script_id = script_id;
	}

	@Column(name = "content_sort")
	public int getContent_sort() {
		return content_sort;
	}

	public void setContent_sort(int content_sort) {
		this.content_sort = content_sort;
	}

	@Column(name = "content_type")
	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	@Column(name = "content_detail", length = 4000)
	public String getContent_detail() {
		return content_detail;
	}

	public void setContent_detail(String content_detail) {
		this.content_detail = content_detail;
	}
}

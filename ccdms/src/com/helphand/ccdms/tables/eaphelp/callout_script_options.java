package com.helphand.ccdms.tables.eaphelp;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="callout_script_options")
public class callout_script_options implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private int script_id;
	private String describe;
	
	public callout_script_options() {
		
	}

	@Id
	@GeneratedValue(generator = "seq_callout_script", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "seq_callout_script", sequenceName = "seq_callout_script")
	@Column(name="id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="script_id")
	public int getScript_id() {
		return script_id;
	}

	public void setScript_id(int script_id) {
		this.script_id = script_id;
	}

	@Column(name="describe")
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}

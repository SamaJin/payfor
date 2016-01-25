package com.helphand.ccdms.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * 报表规则表
 */
@Entity
@Table(name = "report_rule")
@SuppressWarnings("serial")
public class report_rule implements Serializable {
	private int id;
	private String rule_name;
	private String rule_remark;
	
	public report_rule() {
		
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_report_rule", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_report_rule", sequenceName = "seq_1007")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "rule_name")
	public String getRule_name() {
		return rule_name;
	}

	public void setRule_name(String rule_name) {
		this.rule_name = rule_name;
	}

	@Column(name = "rule_remark")
	public String getRule_remark() {
		return rule_remark;
	}

	public void setRule_remark(String rule_remark) {
		this.rule_remark = rule_remark;
	}

}

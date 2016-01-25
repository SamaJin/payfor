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
 * 报表解析规则描述
 */
@Entity
@Table(name = "report_rule_item")
@SuppressWarnings("serial")
public class report_rule_item implements Serializable {
	private int id;
	private int rule_id;
	private String rule_title;
	private String rule_field;
	private int rule_order;
	
	public report_rule_item() {
		
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_report_rule_item", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_report_rule_item", sequenceName = "seq_1008")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "rule_id")
	public int getRule_id() {
		return rule_id;
	}

	public void setRule_id(int rule_id) {
		this.rule_id = rule_id;
	}

	@Column(name = "rule_title")
	public String getRule_title() {
		return rule_title;
	}

	public void setRule_title(String rule_title) {
		this.rule_title = rule_title;
	}

	@Column(name = "rule_field")
	public String getRule_field() {
		return rule_field;
	}

	public void setRule_field(String rule_field) {
		this.rule_field = rule_field;
	}

	@Column(name = "rule_order")
	public int getRule_order() {
		return rule_order;
	}

	public void setRule_order(int rule_order) {
		this.rule_order = rule_order;
	}

}

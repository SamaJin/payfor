package com.helphand.ccdms.tables;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 报表内容表
 */
@Entity
@Table(name = "report_data")
@SuppressWarnings("serial")
public class report_data implements Serializable {
	private int id;
	private int rule_id;
	private Blob report_content;             //JSON数据格式
	private String report_time;
	
	public report_data() {
		
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "seq_report_data", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_report_data", sequenceName = "seq_1009")
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

	@Column(name = "report_content")
	public Blob getReport_content() {
		return report_content;
	}

	public void setReport_content(Blob report_content) {
		this.report_content = report_content;
	}

	@Column(name = "report_time")
	public String getReport_time() {
		return report_time;
	}

	public void setReport_time(String report_time) {
		this.report_time = report_time;
	}
}

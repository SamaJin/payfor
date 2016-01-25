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
 * 定时任务执行时间记录点
 */
@Entity
@Table(name = "system_tag_date")
@SuppressWarnings("serial")
public class system_tag_date implements Serializable {

	private Integer tag_id;
	private String tag_date;

	public system_tag_date() {

	}

	@Id
	@Column(name = "tag_id")
	@GeneratedValue(generator = "seq_system_tag_date", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "seq_system_tag_date", sequenceName = "seq_1011")
	public Integer getTag_id() {
		return tag_id;
	}

	public void setTag_id(Integer tag_id) {
		this.tag_id = tag_id;
	}

	@Column(name = "tag_date")
	public String getTag_date() {
		return tag_date;
	}

	public void setTag_date(String tag_date) {
		this.tag_date = tag_date;
	}

}

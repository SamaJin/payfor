package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.helphand.ccdms.beans.calloutlist_id;

@Entity
@Table(name="calloutlist")
@IdClass(calloutlist_id.class)
@SuppressWarnings("serial")
public class calloutlist implements Serializable {

	private String mobile;
	private String userintent;
	private String agentid;
	private String accdate;
	private String callouttypeedit;

	@Id
	@Column(name="mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name="userintent")
	public String getUserintent() {
		return userintent;
	}

	public void setUserintent(String userintent) {
		this.userintent = userintent;
	}

	@Column(name="agentid")
	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	@Id
	@Column(name="accdate")
	public String getAccdate() {
		return accdate;
	}

	public void setAccdate(String accdate) {
		this.accdate = accdate;
	}

	@Column(name="callouttypeedit")
	public String getCallouttypeedit() {
		return callouttypeedit;
	}

	public void setCallouttypeedit(String callouttypeedit) {
		this.callouttypeedit = callouttypeedit;
	}

}

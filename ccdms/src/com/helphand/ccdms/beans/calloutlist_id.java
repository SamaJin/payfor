package com.helphand.ccdms.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class calloutlist_id implements Serializable {

	private String mobile;
	private String accdate;

	public calloutlist_id() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accdate == null) ? 0 : accdate.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		calloutlist_id other = (calloutlist_id) obj;
		if (accdate == null) {
			if (other.accdate != null)
				return false;
		} else if (!accdate.equals(other.accdate))
			return false;
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		} else if (!mobile.equals(other.mobile))
			return false;
		return true;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAccdate() {
		return accdate;
	}

	public void setAccdate(String accdate) {
		this.accdate = accdate;
	}

}

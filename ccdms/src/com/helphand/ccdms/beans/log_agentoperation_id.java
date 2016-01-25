package com.helphand.ccdms.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class log_agentoperation_id implements Serializable {

	private String timestamp;
	private String time1;

	public log_agentoperation_id() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((time1 == null) ? 0 : time1.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
		log_agentoperation_id other = (log_agentoperation_id) obj;
		if (time1 == null) {
			if (other.time1 != null)
				return false;
		} else if (!time1.equals(other.time1))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTime1() {
		return time1;
	}

	public void setTime1(String time1) {
		this.time1 = time1;
	}

}

package com.helphand.ccdms.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class log_sdr_id implements Serializable {

	private String stimestamp;
	private String answertime;

	public log_sdr_id() {

	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answertime == null) ? 0 : answertime.hashCode());
		result = prime * result + ((stimestamp == null) ? 0 : stimestamp.hashCode());
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
		log_sdr_id other = (log_sdr_id) obj;
		if (answertime == null) {
			if (other.answertime != null)
				return false;
		} else if (!answertime.equals(other.answertime))
			return false;
		if (stimestamp == null) {
			if (other.stimestamp != null)
				return false;
		} else if (!stimestamp.equals(other.stimestamp))
			return false;
		return true;
	}

	public String getStimestamp() {
		return stimestamp;
	}

	public void setStimestamp(String stimestamp) {
		this.stimestamp = stimestamp;
	}

	public String getAnswertime() {
		return answertime;
	}

	public void setAnswertime(String answertime) {
		this.answertime = answertime;
	}

}

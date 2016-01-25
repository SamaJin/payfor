package com.helphand.ccdms.tables.eaphelp;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "callout_script_html")
@SuppressWarnings("serial")
public class callout_script_html implements Serializable {

	private String channel_id;
	private Blob script_html;
	private String script_name;
	private String channel_name;

	public callout_script_html() {

	}

	@Id
	@Column(name = "channel_id")
	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	@Column(name = "script_html")
	public Blob getScript_html() {
		return script_html;
	}

	public void setScript_html(Blob script_html) {
		this.script_html = script_html;
	}

	@Column(name = "script_name")
	public String getScript_name() {
		return script_name;
	}

	public void setScript_name(String script_name) {
		this.script_name = script_name;
	}

	@Column(name = "channel_name")
	public String getChannel_name() {
		return channel_name;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

}

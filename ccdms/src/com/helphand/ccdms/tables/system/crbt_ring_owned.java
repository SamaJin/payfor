package com.helphand.ccdms.tables.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "crbt_ring_owned")
@SuppressWarnings("serial")
public class crbt_ring_owned implements Serializable {

	private int id;
	private String ring_id;
	private String ring_name;
	private String ring_provider;
	private String online_time;
	private String offline_time;
	private String ring_singer;

	public crbt_ring_owned() {

	}

	@Id
	@Column(name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "ring_id")
	public String getRing_id() {
		return ring_id;
	}

	public void setRing_id(String ring_id) {
		this.ring_id = ring_id;
	}

	@Column(name = "ring_name")
	public String getRing_name() {
		return ring_name;
	}

	public void setRing_name(String ring_name) {
		this.ring_name = ring_name;
	}

	@Column(name = "ring_provider")
	public String getRing_provider() {
		return ring_provider;
	}

	public void setRing_provider(String ring_provider) {
		this.ring_provider = ring_provider;
	}

	@Column(name = "online_time")
	public String getOnline_time() {
		return online_time;
	}

	public void setOnline_time(String online_time) {
		this.online_time = online_time;
	}

	@Column(name = "offline_time")
	public String getOffline_time() {
		return offline_time;
	}

	public void setOffline_time(String offline_time) {
		this.offline_time = offline_time;
	}

	@Column(name = "ring_singer")
	public String getRing_singer() {
		return ring_singer;
	}

	public void setRing_singer(String ring_singer) {
		this.ring_singer = ring_singer;
	}

}

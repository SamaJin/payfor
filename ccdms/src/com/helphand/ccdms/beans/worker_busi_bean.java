package com.helphand.ccdms.beans;

/**
 * 员工业绩统计数据
 */
public class worker_busi_bean {

	private String agentid;           // 工号
	private int conn_rate;            // 接续量
	private int own_ring_box;         // 自有铃音2
	private int own_music;            // 自有单曲
	private int own_order_num;        // 自有订购量
	private int coop_music;           // 合作歌曲
	private int ring_open;            // 彩铃开户
	private int total;                // 合计
	private float own_order_rate;     // 自有订购率
	private float busi_success_rate;  // 业务办理率
	private int count_num;            // 总订购量
	private int not_own_music;        // 普通歌曲
	
	private int gift_num;             // 赠送量
	private int own_ring_box3;        // 自有铃音3
	private int free_num;             // 免费铃音
	
	private int enabled_time;         //在线时长
	private int disabled_time;        //禁呼时长
	private int inout_num;            //签入签出次数

	public worker_busi_bean() {

	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public int getConn_rate() {
		return conn_rate;
	}

	public void setConn_rate(int conn_rate) {
		this.conn_rate = conn_rate;
	}

	public int getOwn_ring_box() {
		return own_ring_box;
	}

	public void setOwn_ring_box(int own_ring_box) {
		this.own_ring_box = own_ring_box;
	}

	public int getOwn_music() {
		return own_music;
	}

	public void setOwn_music(int own_music) {
		this.own_music = own_music;
	}

	public int getOwn_order_num() {
		return own_order_num;
	}

	public void setOwn_order_num(int own_order_num) {
		this.own_order_num = own_order_num;
	}

	public int getCoop_music() {
		return coop_music;
	}

	public void setCoop_music(int coop_music) {
		this.coop_music = coop_music;
	}

	public int getRing_open() {
		return ring_open;
	}

	public void setRing_open(int ring_open) {
		this.ring_open = ring_open;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public float getOwn_order_rate() {
		return own_order_rate;
	}

	public void setOwn_order_rate(float own_order_rate) {
		this.own_order_rate = own_order_rate;
	}

	public float getBusi_success_rate() {
		return busi_success_rate;
	}

	public void setBusi_success_rate(float busi_success_rate) {
		this.busi_success_rate = busi_success_rate;
	}

	public int getCount_num() {
		return count_num;
	}

	public void setCount_num(int count_num) {
		this.count_num = count_num;
	}

	public int getNot_own_music() {
		return not_own_music;
	}

	public void setNot_own_music(int not_own_music) {
		this.not_own_music = not_own_music;
	}

	public int getGift_num() {
		return gift_num;
	}

	public void setGift_num(int gift_num) {
		this.gift_num = gift_num;
	}

	public int getOwn_ring_box3() {
		return own_ring_box3;
	}

	public void setOwn_ring_box3(int own_ring_box3) {
		this.own_ring_box3 = own_ring_box3;
	}

	public int getFree_num() {
		return free_num;
	}

	public void setFree_num(int free_num) {
		this.free_num = free_num;
	}

	public int getEnabled_time() {
		return enabled_time;
	}

	public void setEnabled_time(int enabled_time) {
		this.enabled_time = enabled_time;
	}

	public int getDisabled_time() {
		return disabled_time;
	}

	public void setDisabled_time(int disabled_time) {
		this.disabled_time = disabled_time;
	}

	public int getInout_num() {
		return inout_num;
	}

	public void setInout_num(int inout_num) {
		this.inout_num = inout_num;
	}

}

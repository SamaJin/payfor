package com.helphand.ccdms.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.report_column_bean;
import com.helphand.ccdms.beans.report_model_bean;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.tables.notice_message;
import com.helphand.ccdms.tables.eaphelp.task;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class manage_report_model extends ActionSupport {
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private int page, limit;
	private String sql = null;
	private String end_time = null;
	private String start_time = null;
	private JSONObject result_json = null;
	private task o = null;

	public String int_report_model() {
		try {

		} catch (Exception e) {
		}
		return SUCCESS;
	}

	public String list_report_model() {
		try {
			System.out.println("was be call");
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from task where 1=1 ";
			o = new task();
			page_object = daos
					.select_pages(system_session, sql, page, limit, o);
			page_object = page_object == null ? new HibernatePageDemo()
					: page_object;
			list = page_object.getList();
			list = list == null ? new ArrayList<Object>() : list;
			page_json = new HibernatePageJson();
			page_json.setRows(list);
			page_json.setTotal(page_object.getTotal_record_count());
			result_json = JSONObject.fromObject(page_json);
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

}

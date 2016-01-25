package com.helphand.ccdms.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_util;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 呼入业务彩铃管理
 */
public class manage_callin_order_logs extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_callin_order_logs.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageJson page_json = null;
	private String hql = null;
	private List<Object> list = null;
	private JSONObject result_json = null;
	private HibernatePageDemo page_object = null;
	private int limit, page;

	private String area_code = null;
	private String user_phone = null;
	private String start_time = null;
	private String end_time = null;

	//查询
	public String list_callin_order_logs() {
		log.info("Entry manage_callin_order_logs[ list_callin_order_logs()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from commons_callin_order_logs where 1=1";
			//区号
			if(!common_util.isEmpty(area_code)) {
				hql += " and area_code='"+area_code+"'";
			}
			//用户号码
			if(!common_util.isEmpty(user_phone)) {
				hql += " and user_number='"+ user_phone +"'";
			}
			//日期
			if(!common_util.isEmpty(start_time)) {
				String time_sql = " and operate_time >= '"+ start_time +" 00:00:00'";
				if(!common_util.isEmpty(end_time)) {
					time_sql = " and operate_time between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'";
				}
				hql += time_sql;
			}
			
			log.info("exec hql : " + hql);
			page_object = daos.select_pages(system_session, hql, page, limit);
			page_object = (page_object == null ? new HibernatePageDemo() : page_object);
			list = page_object.getList();
			list = (list == null ? new ArrayList<Object>() : list);
			page_json = new HibernatePageJson();
			page_json.setTotal(page_object.getTotal_record_count());
			page_json.setRows(list);
			result_json = JSONObject.fromObject(page_json);
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}


	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
}

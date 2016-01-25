package com.helphand.ccdms.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.tables.report_rule;
import com.helphand.ccdms.tables.report_rule_item;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 报表管理
 */
public class manage_report_rule extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_report_rule.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private String hql = null;
	private int page, limit;
	
	private JSONObject result_json = null;
	private String result_string = null;
	private report_rule rule = null;
	private report_rule_item rule_item = null;

	public String init_report_rule() {
		return SUCCESS;
	}
	
	//列举报表模板
	public String list_report_rule() {
		log.info("Entry manage_report_rule[ list_report_rule()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_rule order by id desc";
			page_object = daos.select_pages(system_session, hql, page, limit);
			page_object = (page_object == null ? new HibernatePageDemo() : page_object);
			list = page_object.getList();
			list = (list == null ? new ArrayList<Object>() : list);
			page_json = new HibernatePageJson();
			page_json.setTotal(page_object.getTotal_record_count());
			page_json.setRows(list);
			result_json = JSONObject.fromObject(page_json);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//列举报表规则模板
	public String list_report_rule_item() {
		log.info("Entry manage_report_rule[ list_report_rule_item()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_rule_item where 1=1";
			if(rule_item != null && rule_item.getRule_id() != 0) {
				hql += " and rule_id="+rule_item.getRule_id();
			}else {
				hql += " and 1=2";
			}
			hql += " order by rule_order";
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			page_json = new HibernatePageJson();
			page_json.setRows(list);
			result_json = JSONObject.fromObject(page_json);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//保存模板
	public String save_report_rule() {
		log.info("Entry manage_report_rule[ save_report_rule()]");
		try {
			result_json = new JSONObject();
			result_string = "保存模板失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(rule == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_rule where id=" + rule.getId();
			log.info("exec hql : " + hql);
			Object obj = daos.select_object(system_session, hql, new report_rule());
			report_rule bean = (obj != null ? (report_rule)obj : new report_rule());
			bean.setRule_name(rule.getRule_name());
			bean.setRule_remark(rule.getRule_remark());
			system_session.saveOrUpdate(bean);
			
			result_string = "保存模板成功";
			result_json.put("return_status", "success");
			result_json.put("return_describe", result_string);
			log.info("exec result : " + result_string);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//删除模板
	public String del_report_rule() {
		log.info("Entry manage_report_rule[ del_report_rule()]");
		try {
			result_json = new JSONObject();
			result_string = "删除模板失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(rule == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_rule where id=" + rule.getId();
			log.info("exec hql : " + hql);
			Object obj = daos.select_object(system_session, hql, new report_rule());
			if(obj != null) {
				report_rule bean = (report_rule)obj;
				hql = "delete from report_rule_item where rule_id="+bean.getId();
				daos.execute_sql(system_session, hql);
				result_string = "删除模板对应的规则成功</br>";
				system_session.delete(bean);
				result_string += "删除模板成功";
				result_json.put("return_status", "success");
				result_json.put("return_describe", result_string);
			}
			log.info("exec result : " + result_string);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	} 
	
	//保存模板规则
	public String save_report_rule_item() {
		log.info("Entry manage_report_rule[ save_report_rule_item()]");
		try {
			result_json = new JSONObject();
			result_string = "保存模板规则失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(rule_item == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_rule_item where id=" + rule_item.getId();
			log.info("exec hql : " + hql);
			Object obj = daos.select_object(system_session, hql, new report_rule_item());
			report_rule_item bean = (obj != null ? (report_rule_item)obj : new report_rule_item());
			bean.setRule_field(rule_item.getRule_field());
			bean.setRule_id(rule_item.getRule_id());
			bean.setRule_order(rule_item.getRule_order());
			bean.setRule_title(rule_item.getRule_title());
			system_session.saveOrUpdate(bean);
			
			result_string = "保存模板规则成功";
			result_json.put("return_status", "success");
			result_json.put("return_describe", result_string);
			log.info("exec result : " + result_string);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//删除模板规则
	public String del_report_rule_item() {
		log.info("Entry manage_report_rule[ del_report_rule_item()]");
		try {
			result_json = new JSONObject();
			result_string = "删除模板规则失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(rule_item == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_rule_item where id=" + rule_item.getId();
			log.info("exec hql : " + hql);
			Object obj = daos.select_object(system_session, hql, new report_rule_item());
			if(obj != null) {
				report_rule_item bean = (report_rule_item)obj;
				system_session.delete(bean);
				result_string = "删除模板规则成功";
				result_json.put("return_status", "success");
				result_json.put("return_describe", result_string);
			}
			log.info("exec result : " + result_string);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	} 

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}

	public String getResult_string() {
		return result_string;
	}

	public void setResult_string(String result_string) {
		this.result_string = result_string;
	}

	public report_rule getRule() {
		return rule;
	}

	public void setRule(report_rule rule) {
		this.rule = rule;
	}

	public report_rule_item getRule_item() {
		return rule_item;
	}

	public void setRule_item(report_rule_item rule_item) {
		this.rule_item = rule_item;
	}
	
}

package com.helphand.ccdms.actions.eaphelp;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.tables.eaphelp.callout_result;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 数据维护管理
 */
public class manage_report_data extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_report.class);
	private HibernateDAO daos = new HibernateDAO();
	private common_tools tools = new common_tools();
	private Session dbs_session = null;
	private Transaction dbs_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private int limit, page;
	private List<Object> list = null;
	private String hql = null;
	
	private JSONObject result_json = null;
	private String result_string = null;
	private callout_result callout_result = null;
	private String script_id;
	private String agent_id;
	private String start_date;
	private String end_date;
	private String callout_phone;
	private String options;
	
	public String init_report_data() {
		log.info("Entry manage_report_data[ init_report_data()]");
		return SUCCESS;
	}
	
	public String init_window() {
		try {
			tools.get_http_session().setAttribute("agent_id", agent_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String window_report() {
		return SUCCESS;
	}

	//查询
	public String list_callout_result() {
		log.info("Entry manage_report_data[ list_callout_result()]");
		try {
			callout_result = (callout_result == null ? new callout_result() : callout_result);
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_result where 1=1";
			
			//脚本ID
			if(!tools.isEmpty(script_id)) {
				hql += " and script_id="+ script_id;
			}else {
				hql += " and 1=2";
			}
			//工号
			if(!tools.isEmpty(agent_id)) {
				hql += " and agent_id='"+ agent_id +"'";
			}
			//号码
			if(!tools.isEmpty(callout_phone)) {
				hql += " and callout_phone='"+ callout_phone +"'";
			}
			//时间
			if(!tools.isEmpty(start_date)) {
				if(!tools.isEmpty(end_date)) {
					hql += " and callout_time between '"+ start_date +" 00:00:00' and '"+ end_date +" 23:59:59'";
				}else {
					hql += " and callout_time >= '"+ start_date +" 00:00:00'";
				}
			}
			//执行结果
			if(!tools.isEmpty(options)) {
				hql += " and result_describe like '%;"+ options +";'";
			}
			
			hql += " order by callout_time";
			log.info("exec hql : " + hql);
			page_object = daos.select_pages(dbs_session, hql, page, limit, callout_result);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			list = (list == null ? new ArrayList<Object>() : list);
			page_json = new HibernatePageJson();
			page_json.setTotal(page_object.getTotal_record_count());
			page_json.setRows(list);
			result_json = JSONObject.fromObject(page_json);
			
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//保存表单
	public String save_callout_result() {
		log.info("Entry manage_report_data[ save_callout_result()]");
		try {
			callout_result = (callout_result == null ? new callout_result() : callout_result);
			result_json = new JSONObject();
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();

			if(!tools.isEmpty(callout_result.getResult_describe())) {
				String temp = callout_result.getResult_describe().replaceAll("\\|;", ";");
				//执行结果
				if(!tools.isEmpty(options)) {
					temp += options+";";
				}
				callout_result.setResult_describe(temp);
			}
			
			//修改
			if(callout_result.getId() != 0) {
				hql = "from callout_result where id="+callout_result.getId();
				Object obj = daos.select_object(dbs_session, hql, new callout_result());
				callout_result bean_result = (callout_result)obj;
				bean_result.setResult_describe(callout_result.getResult_describe());
				dbs_session.update(bean_result);
			}

			result_string = "操作成功";
			result_json.put("resp", "success");
			result_json.put("describe", result_string);
		} catch (Exception e) {
			e.printStackTrace();
			result_string = "操作失败";
			result_json.put("resp", "error");
			result_json.put("describe", result_string);
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
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

	public callout_result getCallout_result() {
		return callout_result;
	}

	public void setCallout_result(callout_result callout_result) {
		this.callout_result = callout_result;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getCallout_phone() {
		return callout_phone;
	}

	public void setCallout_phone(String callout_phone) {
		this.callout_phone = callout_phone;
	}

	public String getScript_id() {
		return script_id;
	}

	public void setScript_id(String script_id) {
		this.script_id = script_id;
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

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}
}

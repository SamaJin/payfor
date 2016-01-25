package com.helphand.ccdms.actions.eaphelp;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.tables.eaphelp.agentidvsname;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 员工管理模块
 */
public class manage_agentidvname_team extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_agentidvname_team.class);
	private HibernateDAO daos = new HibernateDAO();
	private common_tools tools = new common_tools();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private int limit, page;
	private String sql = null;

	private agentidvsname o = null;
	private String agentid;
	private String username;
	private String teamname;

	private JSONObject result_json = null;

	public String init_list_agentidvsname() {
		log.info("Entry manage_script[ init_list_agentidvsname()]");
		return SUCCESS;
	}

	public String list_agentidvsname() {
		log.info("Entry manage_script[ list_agentidvsname()]");
		try {
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from agentidvsname where 1=2 ";
			if (!tools.isEmpty(agentid) || !tools.isEmpty(username)
					|| !tools.isEmpty(teamname)) {
				sql = "from agentidvsname where 1=1 ";
			}
			if (!tools.isEmpty(agentid)) {
				sql += " and agentid='" + agentid + "'";
			}
			if (!tools.isEmpty(username)) {
				sql += " and username='" + username + "'";
			}
			if (!tools.isEmpty(teamname)) {
				sql += " and teamname='" + teamname + "'";
			}
			o = new agentidvsname();
			log.info("exec hql : " + sql);
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

	public String init_modify_agentidvsname() {
		result_json = new JSONObject();
		log.info("Entry manage_script[ init_modify_agentidvsname()]");
		try {
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from agentidvsname where agentid='" + agentid + "'";
			o = new agentidvsname();
			o.setAgentid(agentid);
			o = (agentidvsname) daos.select_object(system_session, sql, o);
			log.info("exec hql : " + sql);
			result_json.put("return_status", "success");
			result_json.put("return_describe", JSONObject.fromObject(o));
		} catch (Exception e) {
			result_json.put("return_status", "failure");
			result_json.put("return_describe", "获取员工信息失败!" + e.getMessage());
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String modify_agentidvsname() {
		result_json = new JSONObject();
		log.info("Entry manage_script[ modify_agentidvsname()]");
		try {
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			system_transaction = system_session.beginTransaction();
			o = new agentidvsname();
			o.setAgentid(agentid);
			sql = "from agentidvsname where agentid='" + agentid + "'";
			o = (agentidvsname) daos.select_object(system_session, sql, o);
			if (StringUtils.isNotBlank(username)) {
				o.setUsername(username);
			}
			if (StringUtils.isNotBlank(teamname)) {
				o.setTeamname(teamname);
			}
			system_session.update(o);
			result_json.put("return_status", "success");
			result_json.put("return_describe", "修改员工信息成功!");
		} catch (Exception e) {
			result_json.put("return_status", "failure");
			result_json.put("return_describe", "修改员工信息失败!" + e.getMessage());
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String delete_agentidvsname() {
		log.info("Entry manage_script[ delete_agentidvsname()]");
		try {
			result_json = new JSONObject();
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "delete from agentidvsname where 1=1 ";
			sql += "and agentid='" + agentid + "'";
			log.info("exec hql : " + sql);
			daos.execute_sql(system_session, sql);
			// --
			result_json.put("return_status", "success");
			result_json.put("return_describe", "删除员工信息成功!");
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
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

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}
}

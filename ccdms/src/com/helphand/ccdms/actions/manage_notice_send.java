package com.helphand.ccdms.actions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.tables.admin_user;
import com.helphand.ccdms.tables.notice_message;
import com.helphand.ccdms.tables.eaphelp.task;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 通知管理模块
 */
public class manage_notice_send extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private common_tools tools = new common_tools();
	private HibernateDAO daos = new HibernateDAO();
	private HttpSession http_session = null;
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private int limit, page;
	private String sql = null;

	private boolean check = true;
	private String check_describe = null;
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	//private notice_message o = null;
	private int notice_id;
	private task o = null;
	private String notice_title; // 消息标题
	private String notice_content; // 消息内容
	private String notice_from; // 消息创建用户
	private String notice_to; // 消息接收用户
	private String notice_status; // 消息状态 0未启用 1启用
	private String notice_create_time; // 消息创建时间

	private admin_user user = null;

	private JSONObject result_json = null;

	public String init_notice_message() {
		return SUCCESS;
	}

	public String add_notice_message() {
		result_json = new JSONObject();
		try {
			check_describe = "";

			http_session = tools.get_http_session();
			user = (admin_user) http_session.getAttribute("ADMINUSER");
			// 字段校验
			if (StringUtils.isBlank(notice_title)) {
				check_describe += "消息标题名不能为空!<br/>";
				check = false;
			}
			if (StringUtils.isBlank(notice_content)) {
				check_describe += "消息内容不能为空!<br/>";
				check = false;
			}
			if (!check) {
				result_json.put("return_status", "failure");
				result_json.put("return_describe", check_describe);
				return SUCCESS;
			}
			// --
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
//			o = new notice_message();
//			o.setNotice_title(notice_title);
//			o.setNotice_content(notice_content);
//			o.setNotice_create_time(format.format(new Date()));
//			o.setNotice_from(String.valueOf(user.getId()));
//			o.setNotice_status("1");
//			o.setNotice_to("");
			system_session.save(o);
			result_json.put("return_status", "success");
			result_json.put("return_describe", "添加消息成功");
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String init_modify_notice_message() {
//		try {
//			result_json = new JSONObject();
//			try {
//				system_session = HibernateSessionSystem.getSession();
//				system_transaction = system_session.beginTransaction();
//				sql = "from notice_message where id=" + notice_id;
//				o = new notice_message();
//				o.setId(notice_id);
//				o = (notice_message) daos.select_object(system_session, sql, o);
//				result_json.put("return_status", "success");
//				result_json.put("return_describe", JSONObject.fromObject(o));
//			} catch (Exception e) {
//				result_json.put("return_status", "failure");
//				result_json
//						.put("return_describe", "获取消息实例失败!" + e.getMessage());
//				daos.rollback_transaction(system_transaction);
//			} finally {
//				daos.commit_transaction(system_transaction);
//				daos.close_session(system_session);
//			}
//
//		} catch (Exception e) {
//		}
		return SUCCESS;
	}

	public String modify_notice_message() {
//		result_json = new JSONObject();
//		try {
//			system_session = HibernateSessionSystem.getSession();
//			system_transaction = system_session.beginTransaction();
//			o = new notice_message();
//			o.setId(notice_id);
//			sql = "from notice_message where id=" + notice_id;
//			o = (notice_message) daos.select_object(system_session, sql, o);
//			if (StringUtils.isNotBlank(notice_title)) {
//				o.setNotice_title(notice_title);
//			}
//			if (StringUtils.isNotBlank(notice_content)) {
//				o.setNotice_content(notice_content);
//			}
//			o.setNotice_create_time(format.format(new Date()));
//			system_session.update(o);
//			result_json.put("return_status", "success");
//			result_json.put("return_describe", "修改消息成功!");
//		} catch (Exception e) {
//			result_json.put("return_status", "failure");
//			result_json.put("return_describe", "修改消息失败!" + e.getMessage());
//			daos.rollback_transaction(system_transaction);
//		} finally {
//			daos.commit_transaction(system_transaction);
//			daos.close_session(system_session);
//		}
		return SUCCESS;
	}

	public String init_list_notice_message() {
		return SUCCESS;
	}

	public String list_notice_message() {
//		try {
//			system_session = HibernateSessionSystem.getSession();
//			system_transaction = system_session.beginTransaction();
//			sql = "from notice_message where 1=1 ";
//			o = new notice_message();
//			page_object = daos
//					.select_pages(system_session, sql, page, limit, o);
//			page_object = page_object == null ? new HibernatePageDemo()
//					: page_object;
//			list = page_object.getList();
//			list = list == null ? new ArrayList<Object>() : list;
//			page_json = new HibernatePageJson();
//			page_json.setRows(list);
//			page_json.setTotal(page_object.getTotal_record_count());
//			result_json = JSONObject.fromObject(page_json);
//		} catch (Exception e) {
//			daos.rollback_transaction(system_transaction);
//		} finally {
//			daos.commit_transaction(system_transaction);
//			daos.close_session(system_session);
//		}
//		return SUCCESS;
		try {
			System.out.println("was be call");
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from task where 1=1 order by id desc ";
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

	public String delete_notice_message() {
		try {
			result_json = new JSONObject();
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "delete from notice_message where 1=1 ";
			sql += "and id=" + notice_id;
			daos.execute_sql(system_session, sql);
			// --
			result_json.put("return_status", "success");
			result_json.put("return_describe", "删除信息成功!");
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

	public int getNotice_id() {
		return notice_id;
	}

	public void setNotice_id(int notice_id) {
		this.notice_id = notice_id;
	}

	public String getNotice_title() {
		return notice_title;
	}

	public void setNotice_title(String notice_title) {
		this.notice_title = notice_title;
	}

	public String getNotice_content() {
		return notice_content;
	}

	public void setNotice_content(String notice_content) {
		this.notice_content = notice_content;
	}

	public String getNotice_from() {
		return notice_from;
	}

	public void setNotice_from(String notice_from) {
		this.notice_from = notice_from;
	}

	public String getNotice_to() {
		return notice_to;
	}

	public void setNotice_to(String notice_to) {
		this.notice_to = notice_to;
	}

	public String getNotice_status() {
		return notice_status;
	}

	public void setNotice_status(String notice_status) {
		this.notice_status = notice_status;
	}

	public String getNotice_create_time() {
		return notice_create_time;
	}

	public void setNotice_create_time(String notice_create_time) {
		this.notice_create_time = notice_create_time;
	}

	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}
}

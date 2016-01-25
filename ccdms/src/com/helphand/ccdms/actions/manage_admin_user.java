package com.helphand.ccdms.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_contants;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.tables.admin_module_group;
import com.helphand.ccdms.tables.admin_user;
import com.helphand.ccdms.tables.admin_user_authority;
import com.opensymphony.xwork2.ActionSupport;

//管理系统用户
@SuppressWarnings("serial")
public class manage_admin_user extends ActionSupport {
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
	// --
	private admin_user user = null;
	private int admin_user_id;
	private String admin_user_name;
	private String admin_user_password;
	private String admin_user_password_new;
	private String admin_user_password_new_repeat;
	private String admin_user_status;
	private String admin_user_type;
	private String admin_user_remarks;
	private boolean check = true;
	private String check_describe = null;
	private int module_group_id;

	// --
	private JSONObject result_json = null;

	public String add_admin_user() {
		result_json = new JSONObject();
		try {
			check_describe = "";
			// --
			// 字段校验
			if (StringUtils.isBlank(admin_user_name)) {
				check_describe += "输入的用户名不能为空!";
				check = false;
			}
			if (StringUtils.isBlank(admin_user_password)) {
				check_describe += "输入的用户密码不能为空!";
				check = false;
			}
			if (StringUtils.isBlank(admin_user_type)) {
				check_describe += "输入的用户类别不能为空!";
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
			user = new admin_user();
			user.setAdmin_user_name(admin_user_name);
			user.setAdmin_user_password(admin_user_password);
			user.setAdmin_user_status(common_contants.ADMIN_USER_STATUS_DISABLE);
			user.setAdmin_user_type(admin_user_type);
			user.setAdmin_user_remarks(admin_user_remarks);
			system_session.save(user);
			result_json.put("return_status", "success");
			result_json.put("return_describe", "添加用户成功");
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String init_modify_admin_user() {
		try {
			result_json = new JSONObject();
			try {
				system_session = HibernateSessionSystem.getSession();
				system_transaction = system_session.beginTransaction();
				sql = "from admin_user where id=:id";
				user = new admin_user();
				user.setId(admin_user_id);
				user = (admin_user) daos.select_object(system_session, sql,
						user);
				result_json.put("return_status", "success");
				result_json.put("return_describe", JSONObject.fromObject(user));
			} catch (Exception e) {
				result_json.put("return_status", "failure");
				result_json
						.put("return_describe", "获取用户实例失败!" + e.getMessage());
				daos.rollback_transaction(system_transaction);
			} finally {
				daos.commit_transaction(system_transaction);
				daos.close_session(system_session);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return SUCCESS;
	}

	public String modify_admin_user() {
		result_json = new JSONObject();
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			user = new admin_user();
			user.setId(admin_user_id);
			sql = "from admin_user where id=:id";
			user = (admin_user) daos.select_object(system_session, sql, user);
			if (StringUtils.isNotBlank(admin_user_status)) {
				user.setAdmin_user_status(admin_user_status);
			}
			if (StringUtils.isNotBlank(admin_user_password)) {
				user.setAdmin_user_password(admin_user_password);
			}
			if (StringUtils.isNotBlank(admin_user_remarks)) {
				user.setAdmin_user_remarks(admin_user_remarks);
			}
			system_session.update(user);
			result_json.put("return_status", "success");
			result_json.put("return_describe", "修改用户成功!");
		} catch (Exception e) {
			result_json.put("return_status", "failure");
			result_json.put("return_describe", "修改用户失败!" + e.getMessage());
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String init_list_admin_user() {
		return SUCCESS;
	}

	public String list_admin_user() {
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_user where 1=1 ";
			user = new admin_user();
			page_object = daos.select_pages(system_session, sql, page, limit,
					user);
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

	public String delete_admin_user() {
		try {
			result_json = new JSONObject();
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "delete from admin_user where 1=1 ";
			sql += "and id=" + admin_user_id;
			daos.execute_sql(system_session, sql);
			// 删除用户对应的组权限
			sql = "delete from admin_user_authority where admin_user_id="
					+ admin_user_id;
			daos.execute_sql(system_session, sql);
			// --
			result_json.put("return_status", "success");
			result_json.put("return_describe", "删除用户成功!");
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String add_admin_user_authority() {
		try {
			result_json = new JSONObject();
			result_json.put("return_status", "error");
			result_json.put("return_describe", "操作失败");
			if (module_group_id == 0 || admin_user_id == 0) {
				return SUCCESS;
			}

			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();

			admin_user_authority authority = new admin_user_authority();
			authority.setAdmin_module_group_id(module_group_id);
			authority.setAdmin_user_id(admin_user_id);
			system_session.save(authority);
			result_json.put("return_status", "success");
			result_json.put("return_describe", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String del_admin_user_authority() {
		try {
			result_json = new JSONObject();
			result_json.put("return_status", "error");
			result_json.put("return_describe", "操作失败");
			if (module_group_id == 0 || admin_user_id == 0) {
				return SUCCESS;
			}
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_user_authority where admin_module_group_id="
					+ module_group_id + " and admin_user_id=" + admin_user_id;
			list = daos.select_list(system_session, sql);
			if (list != null && list.size() > 0) {
				admin_user_authority authority = (admin_user_authority) list
						.get(0);
				system_session.delete(authority);
			}
			result_json.put("return_status", "success");
			result_json.put("return_describe", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String all_module_group_name() {
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_user_authority where admin_user_id="
					+ admin_user_id;
			list = daos.select_list(system_session, sql);
			List<Integer> group_id_list = new ArrayList<Integer>();
			for (int i = 0; i < list.size(); i++) {
				admin_user_authority bean_authority = (admin_user_authority) list
						.get(i);
				group_id_list.add(bean_authority.getAdmin_module_group_id());
			}

			sql = "from admin_module_group";
			list = daos.select_list(system_session, sql);
			list = (list == null ? new ArrayList<Object>() : list);
			for (int i = 0; i < list.size(); i++) {
				admin_module_group bean_module_group = (admin_module_group) list
						.get(i);
				if (group_id_list.contains(bean_module_group.getId())) {
					bean_module_group.setModule_status(1);
				}
			}
			page_json = new HibernatePageJson();
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

	public String init_modify_admin_user_password() {
		return SUCCESS;
	}

	public String modify_admin_user_password() {
		result_json = new JSONObject();
		check_describe = "";
		try {
			if (StringUtils.isBlank(admin_user_password)) {
				check = false;
				check_describe += "输入的旧密码不能为空!<br>";
			}
			if (StringUtils.isBlank(admin_user_password_new)) {
				check = false;
				check_describe += "输入的新密码不能为空!<br>";
			}
			if (StringUtils.isBlank(admin_user_password_new_repeat)) {
				check = false;
				check_describe += "输入重复的新密码不能为空!<br>";
			}
			if (!StringUtils.equals(admin_user_password_new,
					admin_user_password_new_repeat)) {
				check = false;
				check_describe += "两次输入的新密码不一致!<br>";
			}
			if (!check) {
				result_json.put("return_status", "failure");
				result_json.put("return_describe", check_describe);
				return SUCCESS;
			}
			// --
			http_session = tools.get_http_session();
			admin_user u = (admin_user) http_session.getAttribute("ADMINUSER");
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_user where admin_user_name=:admin_user_name and admin_user_password=:admin_user_password) ";
			user = new admin_user();
			user.setAdmin_user_name(u.getAdmin_user_name());
			user.setAdmin_user_password(admin_user_password);
			user = (admin_user) daos.select_object(system_session, sql, user);
			if (user == null) {
				result_json.put("return_status", "failure");
				result_json.put("return_describe", "用户不存在或者密码错误!");
				return SUCCESS;
			} else {
				user.setAdmin_user_password(admin_user_password_new);
				system_session.update(user);
				result_json.put("return_status", "success");
				result_json.put("return_describe", "用户密码修改成功,请退出使用新密码登录!");
			}
			return SUCCESS;
		} catch (Exception e) {
			result_json.put("return_status", "failure");
			result_json.put("return_describe", e.getMessage());
			daos.rollback_transaction(system_transaction);
			return ERROR;
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
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

	public String getAdmin_user_name() {
		return admin_user_name;
	}

	public void setAdmin_user_name(String admin_user_name) {
		this.admin_user_name = admin_user_name;
	}

	public String getAdmin_user_password() {
		return admin_user_password;
	}

	public void setAdmin_user_password(String admin_user_password) {
		this.admin_user_password = admin_user_password;
	}

	public String getAdmin_user_password_new() {
		return admin_user_password_new;
	}

	public void setAdmin_user_password_new(String admin_user_password_new) {
		this.admin_user_password_new = admin_user_password_new;
	}

	public String getAdmin_user_status() {
		return admin_user_status;
	}

	public void setAdmin_user_status(String admin_user_status) {
		this.admin_user_status = admin_user_status;
	}

	public String getAdmin_user_type() {
		return admin_user_type;
	}

	public void setAdmin_user_type(String admin_user_type) {
		this.admin_user_type = admin_user_type;
	}

	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}

	public int getAdmin_user_id() {
		return admin_user_id;
	}

	public void setAdmin_user_id(int admin_user_id) {
		this.admin_user_id = admin_user_id;
	}

	public int getModule_group_id() {
		return module_group_id;
	}

	public void setModule_group_id(int module_group_id) {
		this.module_group_id = module_group_id;
	}

	public String getAdmin_user_password_new_repeat() {
		return admin_user_password_new_repeat;
	}

	public void setAdmin_user_password_new_repeat(
			String admin_user_password_new_repeat) {
		this.admin_user_password_new_repeat = admin_user_password_new_repeat;
	}

	public String getAdmin_user_remarks() {
		return admin_user_remarks;
	}

	public void setAdmin_user_remarks(String admin_user_remarks) {
		this.admin_user_remarks = admin_user_remarks;
	}
}

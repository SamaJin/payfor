package com.helphand.ccdms.actions;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.dbs.*;
import com.helphand.ccdms.tables.admin_module;
import com.helphand.ccdms.tables.admin_module_group_describe;
import com.opensymphony.xwork2.*;

//管理系统功能模块
@SuppressWarnings("serial")
public class manage_admin_module extends ActionSupport {
	private Log log = LogFactory.getLog(manage_admin_module.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private JSONObject result_json = null;
	private String result_string = null;
	private int page, limit;
	private String sql = null;
	// --
	private admin_module module = null;
	private Integer module_id;
	private Integer parent_id;
	private String module_name;
	private String module_type;
	private String module_uri;
	private String module_describe;
	private boolean check = true;
	private String check_describe = null;
	
	public String init_list_admin_module() {
		return SUCCESS;
	}
	
	//获取父节点
	public String list_admin_module_name() {
		log.info("Entry manage_admin_module[ list_admin_module_name()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			list = (list == null ? new ArrayList<Object>() : list);
			admin_module parent_module = new admin_module();
			parent_module.setId(0);
			parent_module.setModule_name("父节点");
			list.add(parent_module);
			
			sql = "from admin_module where parent_id=0";
			log.info("exec hql : " + sql);
			List<Object> obj = daos.select_list(system_session, sql);
			list.addAll(obj);
			
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

	//保存
	public String save_admin_module() {
		log.info("Entry manage_admin_module[ save_admin_module()]");
		result_json = new JSONObject();
		check_describe = "";
		try {
			if (StringUtils.isBlank(module_name)) {
				check = false;
				check_describe += "输入的功能模块名称不能为空!</br>";
			}
			if (StringUtils.isBlank(module_type)) {
				check = false;
				check_describe += "输入的功能模块类型不能为空!</br>";
			}
			if (StringUtils.isBlank(module_uri)) {
				check = false;
				check_describe += "输入的功能模块连接不能为空!</br>";
			}
			if (parent_id == null) {
				check = false;
				check_describe += "父节点异常!";
			}
			if (!check) {
				result_json.put("return_status", "failure");
				result_json.put("return_describe", check_describe);
				return SUCCESS;
			}
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			
			//修改
			if(module_id != null) {
				sql = "from admin_module where id="+module_id;
				log.info("exec hql : " + sql);
				Object obj = daos.select_object(system_session, sql, new admin_module());
				if(obj != null) {
					admin_module bean = (admin_module)obj;
					bean.setModule_describe(module_describe);
					bean.setModule_name(module_name);
					bean.setModule_type(module_type);
					bean.setModule_uri(module_uri);
					bean.setParent_id(parent_id);
					system_session.update(bean);
				}
			}else {
				module = new admin_module();
				module.setModule_describe(module_describe);
				module.setModule_name(module_name);
				module.setModule_type(module_type);
				module.setModule_uri(module_uri);
				module.setParent_id(parent_id);
				system_session.save(module);
			}
			result_json.put("return_status", "success");
			result_json.put("return_describe", "保存系统模块成功!");
		} catch (Exception e) {
			result_json.put("return_status", "failure");
			result_json.put("return_describe", "保存系统模块失败!" + e.getMessage());
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	//查询
	public String list_admin_module() {
		log.info("Entry manage_admin_module[ list_admin_module()]");
		try {
			result_json = new JSONObject();
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_module where 1=1";
			sql += " order by module_describe";
			module = new admin_module();
			page_object = daos.select_pages(system_session, sql, page, limit, module);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			list = list == null ? new ArrayList<Object>() : list;
			page_json = new HibernatePageJson();
			page_json.setRows(list);
			page_json.setTotal(page_object.getTotal_record_count());
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
	
	//查询详细
	public String info_admin_module() {
		log.info("Entry manage_admin_module[ info_admin_module()]");
		try {
			result_json = new JSONObject();
			result_string = "获取数据失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(module_id == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_module where id="+module_id;
			log.info("exec hql : " + sql);
			Object obj = daos.select_object(system_session, sql, new admin_module());
			if(obj != null) {
				admin_module bean = (admin_module)obj;
				result_string = "获取数据成功";
				result_json.put("return_status", "success");
				result_json.put("bean", bean);
			}
			log.info("exec result : " + result_string);
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//获取模块组名称
	public String list_module_group_name() {
		log.info("Entry manage_admin_module[ list_module_group_name()]");
		try {
			result_json = new JSONObject();
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_module_group";
			log.info("exec hql : " + sql);
			list = daos.select_list(system_session, sql);
			list = (list == null ? new ArrayList<Object>() : list);
			page_json = new HibernatePageJson();
			page_json.setRows(list);
			page_json.setTotal(list.size());
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

	//删除
	public String delete_admin_module() {
		log.info("Entry manage_admin_module[ delete_admin_module()]");
		try {
			result_json = new JSONObject();
			result_string = "操作失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(module_id == null) {
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			sql = "from admin_module where id="+module_id;
			log.info("exec hql : " + sql);
			Object obj = daos.select_object(system_session, sql, new admin_module());
			admin_module module = (admin_module)obj;
			system_session.delete(module);
			
			//删除模块对应的组权限
			sql = "from admin_module_group_describe where admin_module_id="+module.getId();
			List<Object> obj_list = daos.select_list(system_session, sql);
			if(obj_list != null) {
				for(int i=0; i<obj_list.size(); i++) {
					system_session.delete((admin_module_group_describe)obj_list.get(i));
				}
			}
			
			result_string = "操作成功";
			result_json.put("return_status", "success");
			result_json.put("return_describe", result_string);
			log.info("exec result : " + result_string);
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

	public Integer getModule_id() {
		return module_id;
	}

	public void setModule_id(Integer module_id) {
		this.module_id = module_id;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getModule_type() {
		return module_type;
	}

	public void setModule_type(String module_type) {
		this.module_type = module_type;
	}

	public String getModule_uri() {
		return module_uri;
	}

	public void setModule_uri(String module_uri) {
		this.module_uri = module_uri;
	}

	public String getModule_describe() {
		return module_describe;
	}

	public void setModule_describe(String module_describe) {
		this.module_describe = module_describe;
	}

	public String getResult_string() {
		return result_string;
	}

	public void setResult_string(String result_string) {
		this.result_string = result_string;
	}
}

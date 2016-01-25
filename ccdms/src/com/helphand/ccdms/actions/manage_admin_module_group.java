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
import com.helphand.ccdms.tables.admin_module;
import com.helphand.ccdms.tables.admin_module_group;
import com.helphand.ccdms.tables.admin_module_group_describe;
import com.helphand.ccdms.tables.admin_user_authority;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 分组管理
 */
public class manage_admin_module_group extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_admin_module_group.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private String hql = null;
	
	private JSONObject result_json = null;
	private String result_string = null;
	private int page, limit;
	private admin_module_group module_group = null;
	private Integer module_id = null;
	private Integer module_group_id = null;
	
	public String init_list_admin_module_group() {
		return SUCCESS;
	}
	
	//查询
	public String list_admin_module_group() {
		log.info("Entry manage_admin_module_group[ list_admin_module_group()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from admin_module_group";
			log.info("exec hql : " + hql);
			page_object = daos.select_pages(system_session, hql, page, limit, new manage_admin_module_group());
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
	
	//保存
	public String save_admin_module_group() {
		log.info("Entry manage_admin_module_group[ save_admin_module_group()]");
		try {
			result_json = new JSONObject();
			result_string = "操作失败";
			result_json.put("resp", "error");
			result_json.put("describe", result_string);
			module_group = (module_group == null ? new admin_module_group() : module_group);
			if(common_util.isEmpty(module_group.getModule_group_name())) {
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			system_session.save(module_group);
			result_string = "操作成功";
			result_json.put("resp", "success");
			result_json.put("describe", result_string);
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
	
	//删除
	public String del_admin_module_group() {
		try {
			result_json = new JSONObject();
			result_string = "操作失败";
			result_json.put("resp", "error");
			result_json.put("describe", result_string);
			module_group = (module_group == null ? new admin_module_group() : module_group);
			if(module_group.getId() == 0) {
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from admin_module_group where id=" + module_group.getId();
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			if(list == null || list.size() == 0) {
				return SUCCESS;
			}
			
			//删除组对应的模块权限
			admin_module_group bean = (admin_module_group)list.get(0);
			system_session.delete(bean);
			hql = "from admin_module_group_describe where admin_module_group_id="+bean.getId();
			List<Object> obj_list = daos.select_list(system_session, hql);
			if(obj_list != null) {
				for(int i=0; i<obj_list.size(); i++) {
					system_session.delete((admin_module_group_describe)obj_list.get(i));
				}
			}
			
			//删除组对应的用户权限
			hql = "from admin_user_authority where admin_module_group_id="+bean.getId();
			obj_list = daos.select_list(system_session, hql);
			if(obj_list != null) {
				for(int i=0; i<obj_list.size(); i++) {
					system_session.delete((admin_user_authority)obj_list.get(i));
				}
			}
			
			result_string = "操作成功";
			result_json.put("resp", "success");
			result_json.put("describe", result_string);
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
	
	//获取授权信息
	public String list_admin_module_authority() {
		log.info("Entry manage_admin_module_group[ list_admin_module_authority()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from admin_module order by module_describe";
			List<Object> obj_module = daos.select_list(system_session, hql);
			if(obj_module == null) {
				return SUCCESS;
			}
			
			hql = "from admin_module_group_describe where admin_module_group_id="+module_group.getId();
			List<Object> obj_describe = daos.select_list(system_session, hql);
			if(obj_describe == null) {
				return SUCCESS;
			}
			
			for(int i=0; i<obj_describe.size(); i++) {
				admin_module_group_describe describe = (admin_module_group_describe)obj_describe.get(i);
				for(int index=0; index<obj_module.size(); index++) {
					admin_module module = (admin_module)obj_module.get(index);
					if(module.getId() == describe.getAdmin_module_id()) {
						module.setModule_status(1);
						break;
					}
				}
			}
			
			page_json = new HibernatePageJson();
			page_json.setRows(obj_module);
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
	
	//授权
	public String add_to_admin_group() {
		log.info("Entry manage_admin_module_group[ add_to_admin_group()]");
		try {
			result_json = new JSONObject();
			result_string = "操作失败";
			result_json.put("resp", "error");
			result_json.put("describe", result_string);
			if(module_id == null || module_group_id == null) {
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from admin_module_group_describe where admin_module_group_id=" + module_group_id
				+ " and admin_module_id=" + module_id;
			list = daos.select_list(system_session, hql);
			if(list != null && list.size() > 0) {
				return SUCCESS;
			}
			admin_module_group_describe describe = new admin_module_group_describe();
			describe.setAdmin_module_group_id(module_group_id);
			describe.setAdmin_module_id(module_id);
			system_session.save(describe);
			result_string = "操作成功";
			result_json.put("resp", "success");
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//取消授权
	public String del_from_admin_group() {
		log.info("Entry manage_admin_module_group[ del_from_admin_group()]");
		try {
			result_json = new JSONObject();
			result_string = "操作失败";
			result_json.put("resp", "error");
			result_json.put("describe", result_string);
			if(module_id == null || module_group_id == null) {
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from admin_module_group_describe where admin_module_group_id=" + module_group_id
				+ " and admin_module_id=" + module_id;
			list = daos.select_list(system_session, hql);
			if(list != null && list.size() > 0) {
				admin_module_group_describe describe = (admin_module_group_describe)list.get(0);
				system_session.delete(describe);
				result_string = "操作成功";
				result_json.put("resp", "success");
			}
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

	public String getResult_string() {
		return result_string;
	}

	public void setResult_string(String result_string) {
		this.result_string = result_string;
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

	public admin_module_group getModule_group() {
		return module_group;
	}

	public void setModule_group(admin_module_group module_group) {
		this.module_group = module_group;
	}

	public Integer getModule_id() {
		return module_id;
	}

	public void setModule_id(Integer module_id) {
		this.module_id = module_id;
	}

	public Integer getModule_group_id() {
		return module_group_id;
	}

	public void setModule_group_id(Integer module_group_id) {
		this.module_group_id = module_group_id;
	}

}

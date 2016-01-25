package com.helphand.ccdms.actions;

import java.util.*;

import net.sf.json.*;

import org.hibernate.*;

import javax.servlet.http.*;

import org.apache.commons.lang3.*;

import com.helphand.ccdms.beans.*;
import com.helphand.ccdms.commons.*;
import com.helphand.ccdms.dbs.*;
import com.helphand.ccdms.tables.*;
import com.opensymphony.xwork2.*;

@SuppressWarnings("serial")
public class manage_authentication extends ActionSupport {
	private common_tools tools = new common_tools();
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HttpSession http_session = null;
	private String sql = "";
	// --
	private admin_user user = null;
	private String admin_user_name = null;
	private String admin_user_password = null;
	private admin_module module = null;
	private String type=null;

	// --
	private JSONObject result_json = null;
	private boolean check = true;
	private String result_string = null;

	public String system_login() {
		try {
			result_json = new JSONObject();
			result_string = "";
			//String type="0";
			// --
			if (StringUtils.isBlank(admin_user_name)) {
				result_string = "输入的用户名不能为空!";
				check = false;
			}
		
			if (StringUtils.isBlank(admin_user_password)) {
				result_string = "输入的用户密码不能为空!";
				check = false;
			}
			// --
			if (!check) {
				result_json.put("return_status", "failure");
				result_json.put("return_describe", result_string);
			}
			// --
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			
			user = new admin_user();
			user.setAdmin_user_name(admin_user_name);
			user.setAdmin_user_password(admin_user_password);
			if(type!=null&&type.equals("1"))
			{
				//通过前台调用登录
				sql = "from admin_user where admin_user_name=:admin_user_name";
			}else
			{
				sql = "from admin_user where admin_user_name=:admin_user_name and admin_user_password=:admin_user_password ";
			}
			user = (admin_user) daos.select_object(system_session, sql, user);
			if (user != null) {
				http_session = tools.get_http_session();
				http_session.setAttribute("ADMINUSER", user);
				result_string += "登录成功!";
				result_json.put("return_status", "success");
				result_json.put("return_describe", result_string);
			} else {
				result_string += "登录失败!";
				http_session = tools.get_http_session();
				http_session.setAttribute("ADMINUSER", null);
				result_json.put("return_status", "failure");
				result_json.put("return_describe", result_string);
			}
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	public String system_logout() {
		try {
			http_session = tools.get_http_session();
			http_session.setAttribute("ADMINUSER", null);
			http_session.setAttribute("ADMINMENU", null);
			result_json.put("return_status", "success");
		} catch (Exception e) {

		}
		return SUCCESS;
	}

	public String init_system_index() {
		try {

			System.out.println("was be callback");
			if(type!=null&&type.equals("1"))
			{
				system_login();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return SUCCESS;
	}

	public String init_system_menu() {
		try {
			result_json = new JSONObject();
			http_session = tools.get_http_session();
			user = (admin_user) http_session.getAttribute("ADMINUSER");
			if(user==null)return SUCCESS;
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			// --
			// 获取用户权限
			admin_user_authority user_authority = new admin_user_authority();
			user_authority.setAdmin_user_id(user.getId());
			sql = "from admin_module where id in(select admin_module_id from admin_module_group_describe where admin_module_group_id in(select admin_module_group_id from admin_user_authority where admin_user_id=" + user.getId() + "))";
			List<Object> list_admin_module = daos.select_list(system_session, sql);
			list_admin_module = list_admin_module == null ? new ArrayList<Object>() : list_admin_module;
			// --
			// 是否是超级用户
			if (StringUtils.equalsIgnoreCase(common_contants.ADMIN_USER_TYPE_ADMIN, user.getAdmin_user_type())) {
				list_admin_module.addAll(init_admin_menu());
			}
			// --
			List<beans_system_menu> list_parent = null;
			List<beans_system_menu> list_children = null;
			admin_module m1 = null, m2 = null;
			beans_system_menu item1 = null, item2 = null;
			list_parent = new ArrayList<beans_system_menu>();
			for (int i = 0; i < list_admin_module.size(); i++) {
				list_children = new ArrayList<beans_system_menu>();
				m1 = (admin_module) list_admin_module.get(i);
				if (m1.getParent_id() == 0) {
					item1 = new beans_system_menu();
					item1.setText(m1.getModule_name());
					item1.setLeaf(false);
					for (int j = 0; j < list_admin_module.size(); j++) {
						m2 = (admin_module) list_admin_module.get(j);
						if (m1.getId() == m2.getParent_id()) {
							if (StringUtils.equals(m2.getModule_type(), common_contants.ADMIN_MODULE_NAVIGATOR)) {
								item2 = new beans_system_menu();
								item2.setText("<a href=javascript:add_tab('" + m2.getModule_name() + "','" + m2.getModule_uri() + "')>" + m2.getModule_name() + "</a>");
								item2.setLeaf(true);
								list_children.add(item2);
							}
						}
					}
					item1.setChildren(list_children);
					list_parent.add(item1);
				}
			}
			
			//权限
			http_session.setAttribute("USER_AUTHORITY", list_admin_module);
			result_json.put("return_status", "success");
			result_json.put("children", JSONArray.fromObject(list_parent));
		} catch (Exception e) {
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}

	private List<Object> init_admin_menu() {
		List<Object> lim = null;
		try {
			// 初始化菜单字符
			lim = new ArrayList<Object>();
			String menu_array_string = "";
			menu_array_string += "-1,0,系统超级管理,1,#;";

			menu_array_string += "-11,-1,系统用户,1,initListAdminUser.do;";
			menu_array_string += "-12,-1,列举系统用户,0,listAdminUser.do;";
			menu_array_string += "-13,-1,添加系统用户,0,addAdminUser.do;";
			menu_array_string += "-14,-1,删除系统用户,0,deleteAdminUser.do;";
			menu_array_string += "-15,-1,初始化系统用户信息,0,initModifyAdminUser.do;";
			menu_array_string += "-16,-1,修改系统用户,0,modifyAdminUser.do;";
			menu_array_string += "-17,-1,授权系统用户,0,addAdminUserAuthority.do;";
			menu_array_string += "-18,-1,取消授权系统用户,0,delAdminUserAuthority.do;";
			menu_array_string += "-19,-1,列举模块组名称,0,allModuleGroupName.do;";

			
			menu_array_string += "-21,-1,系统模块,1,initListAdminModule.do;";
			menu_array_string += "-22,-1,列举系统功能模块,0,listAdminModule.do;";
			menu_array_string += "-23,-1,列举系统功能模块组名称,0,listModuleGroupName.do;";
			menu_array_string += "-24,-1,列举系统功能模块名称,0,listAdminModuleName.do;";
			menu_array_string += "-25,-1,系统功能模块信息,0,infoAdminModule.do;";
			menu_array_string += "-26,-1,保存系统功能模块,0,saveAdminModule.do;";
			menu_array_string += "-27,-1,删除系统功能模块,0,deleteAdminModule.do;";
			
			
			menu_array_string += "-31,-1,系统模块组,1,initListAdminModuleGroup.do;";
			menu_array_string += "-32,-1,列举系统功能模块组,0,listAdminModuleGroup.do;";
			menu_array_string += "-33,-1,保存系统功能模块组,0,saveAdminModuleGroup.do;";
			menu_array_string += "-34,-1,删除系统功能模块组,0,delAdminModuleGroup.do;";
			menu_array_string += "-35,-1,列举系统功能模块组权限,0,listAdminModuleAuthority.do;";
			menu_array_string += "-36,-1,授权系统功能模块组,0,addToAdminGroup.do;";
			menu_array_string += "-37,-1,取消授权系统功能模块组,0,delFromAdminGroup.do;";

			//
			String[] menu_strings = StringUtils.split(menu_array_string, ";");
			String menu_string = null;
			String[] menu_item_strings = null;
			for (int i = 0; i < menu_strings.length; i++) {
				menu_string = menu_strings[i];
				menu_item_strings = StringUtils.split(menu_string, ",");
				module = new admin_module();
				module.setId(Integer.parseInt(menu_item_strings[0]));
				module.setParent_id(Integer.parseInt(menu_item_strings[1]));
				module.setModule_name(menu_item_strings[2]);
				module.setModule_type(StringUtils.equals(menu_item_strings[3], "1") ? common_contants.ADMIN_MODULE_NAVIGATOR : common_contants.ADMIN_MODULE_FUNCTION);
				module.setModule_uri(menu_item_strings[4]);
				lim.add(module);
			}
			return lim;
		} catch (Exception e) {
			return new ArrayList<Object>();
		}
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

	public JSONObject getResult_json() {
		return result_json;
	}

	public void setResult_json(JSONObject result_json) {
		this.result_json = result_json;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

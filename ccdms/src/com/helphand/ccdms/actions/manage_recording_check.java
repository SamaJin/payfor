package com.helphand.ccdms.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.commons.common_util;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.tables.admin_user;
import com.helphand.ccdms.tables.commons_records_list;
import com.helphand.ccdms.tables.recording_check_result;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 质检管理
 */
public class manage_recording_check extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_recording_check.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private int page, limit;
	private String hql = null;
	
	private common_tools tools = new common_tools();
	private HttpSession http_session = null;
	private admin_user user = null;
	
	private JSONObject result_json = null;
	private String result_string = null;
	private recording_check_result check_result = null;
	private commons_records_list records_list = null;
	private Integer recording_id;
	private String start_time;
	private String end_time;
	private Integer start_result;
	private Integer end_result;
	private Integer is_check;
	private Integer check_user_id;
	private String call_type;

	public String init_recording_check() {
		return SUCCESS;
	}
	
	//查询质检结果
	public String list_recording_check_result() {
		log.info("Entry manage_recording_check[ list_recording_check_result()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from recording_check_result where 1=1";
			http_session = tools.get_http_session();
			user = (admin_user) http_session.getAttribute("ADMINUSER");
			
			//查询条件
			if(check_result != null) {
				
				//录音类型
				if(!common_util.isEmpty(check_result.getRecording_type())) {
					hql += " and recording_type = '"+ check_result.getRecording_type() +"'";
				}
				//质检用户
				if(check_user_id != null) {
					if(check_user_id != -1) {
						hql += " and check_user_id="+check_user_id;
					}
				}else {
					//只能看到自己的质检结果
					hql += " and check_user_id="+user.getId();
				}
				//抽检时间
				if(!common_util.isEmpty(start_time)) {
					String time_sql = " and check_time >= '"+ start_time +"'";
					if(!common_util.isEmpty(end_time)) {
						time_sql = " and check_time between '"+ start_time +"' and '"+ end_time +"'";
					}
					hql += time_sql;
				}
				//业务范围
				if(!common_util.isEmpty(check_result.getCall_type())) {
					hql += " and call_type='"+ check_result.getCall_type() +"'";
				}
				//错误类型
				if(!common_util.isEmpty(check_result.getError_type())) {
					hql += " and error_type='"+ check_result.getError_type() +"'";
				}
				//评分
				if(start_result != null) {
					hql += " and check_result>="+start_result;
				}
				if(end_result != null) {
					hql += " and check_result<="+end_result;
				}
				//复检
				if(is_check != null) {
					hql += (is_check == 1 ? (" and recheck_result is not null") : (" and recheck_result is null"));
				}
			}else {
				//只能看到自己的质检结果
				hql += " and check_user_id="+user.getId();
			}
			
			hql += " order by check_time desc";
			log.info("exec hql : " + hql);
			page_object = daos.select_pages(system_session, hql, page, limit);
			page_object = (page_object == null ? new HibernatePageDemo() : page_object);
			list = page_object.getList();
			list = (list == null ? new ArrayList<Object>() : list);
			
			hql = "select id,admin_user_name from admin_user";
			List<Object> obj = daos.select_list(system_session, hql);
			for(int i=0; i<list.size(); i++) {
				recording_check_result check_bean = (recording_check_result)list.get(i);
				
				for(int num=0; num<obj.size(); num++) {
					Object[] objs = (Object[])obj.get(num);
					String user_id = String.valueOf(objs[0]);
					String user_name = String.valueOf(objs[1]);
					//显示用户名
					if(user_id.equals(check_bean.getCheck_user_id()+"")) {
						check_bean.setCheck_user_name(user_name);
						break;
					}
				}
			}
			
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
	
	//质检详细信息
	public String info_recording_check_result() {
		log.info("Entry manage_recording_check[ info_recording_check_result()]");
		try {
			result_json = new JSONObject();
			result_string = "获取信息失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(check_result == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from recording_check_result where id=" + check_result.getId();
			Object obj = daos.select_object(system_session, hql, new recording_check_result());
			if(obj != null) {
				recording_check_result bean = (recording_check_result)obj;
				result_string = "成功获取信息";
				result_json.put("return_status", "success");
				result_json.put("return_bean", bean);
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
	
	//录音详细信息
	public String info_commons_records_list() {
		log.info("Entry manage_recording_check[ info_commons_records_list()]");
		try {
			result_json = new JSONObject();
			result_string = "获取信息失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(recording_id == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from commons_records_list where id="+recording_id;
			log.info("exec hql : " + hql);
			Object obj = daos.select_object(system_session, hql, new commons_records_list());
			if(obj != null) {
				records_list = (commons_records_list)obj;
				result_string = "成功获取录音信息";
				result_json.put("return_status", "success");
				result_json.put("return_bean", records_list);
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
	
	//保存质检结果
	public String save_recording_check_result() {
		log.info("Entry manage_recording_check[ save_recording_check_result()]");
		try {
			result_json = new JSONObject();
			result_string = "保存失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(check_result == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from recording_check_result where id="+check_result.getId();
			Object obj = daos.select_object(system_session, hql, new recording_check_result());
			if(obj != null) {
				recording_check_result bean = (recording_check_result)obj;
				if(!common_util.isEmpty(bean.getRecheck_result())) {
					result_string = "已经复检不能修改";
					result_json.put("return_status", "error");
					result_json.put("return_describe", result_string);
					return SUCCESS;
				}
				log.info("exec info : 修改质检信息");
				bean.setError_type(check_result.getError_type());
				bean.setCheck_type(check_result.getCheck_type());
				bean.setCheck_result(check_result.getCheck_result());
				bean.setResult_describe(check_result.getResult_describe().trim());
				bean.setCall_type(check_result.getCall_type());
				bean.setCall_leaf_type(check_result.getCall_leaf_type());
				bean.setRecording_type(check_result.getRecording_type());  //录音类型
				system_session.update(bean);
			}else {
				log.info("exec info : 新增质检信息");
				recording_check_result bean = new recording_check_result();
				bean.setAgent_id(check_result.getAgent_id());              //工号
				bean.setRecording_time(check_result.getRecording_time());  //录音时间
				bean.setOrigcaller(check_result.getOrigcaller());          //源主叫
				bean.setOrigcalled(check_result.getOrigcalled());          //源被叫
				bean.setCaller(check_result.getCaller());                  //主叫
				bean.setCalled(check_result.getCalled());                  //被叫
				bean.setCheck_time(common_util.dateFormatDTS(new Date())); //抽检时间
				bean.setRecording_type(check_result.getRecording_type());  //录音类型
				bean.setCall_type(check_result.getCall_type());            //业务范围
				bean.setCall_leaf_type(check_result.getCall_leaf_type());  //业务类型
				bean.setError_type(check_result.getError_type());          //差错类型
				bean.setCheck_type(check_result.getCheck_type());          //考核类别
				bean.setCheck_result(check_result.getCheck_result());      //评分
				bean.setResult_describe(check_result.getResult_describe().trim());//评语
				bean.setRecording_id(check_result.getRecording_id());      //录音ID
				bean.setFile_name(check_result.getFile_name());            //文件路径
				
				http_session = tools.get_http_session();
				user = (admin_user) http_session.getAttribute("ADMINUSER");
				bean.setCheck_user_id(user.getId());                       //质检用户
				system_session.save(bean);
			}
			
			result_string = "保存成功";
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
	
	//复检
	public String exec_recheck_result() {
		log.info("Entry manage_recording_check[ exec_recheck_result()]");
		try {
			result_json = new JSONObject();
			result_string = "保存失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(check_result == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from recording_check_result where id="+check_result.getId();
			Object obj = daos.select_object(system_session, hql, new recording_check_result());
			if(obj != null) {
				recording_check_result bean = (recording_check_result)obj;
				
				log.info("exec info : 复检");
				http_session = tools.get_http_session();
				user = (admin_user) http_session.getAttribute("ADMINUSER");
				bean.setRecheck_user_id(user.getId());
				bean.setRecheck_result(check_result.getRecheck_result());
				system_session.update(bean);
				
				result_string = "保存成功";
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
	
	//删除质检结果
	public String del_recording_check_result() {
		log.info("Entry manage_recording_check[ del_recording_check_result()]");
		try {
			result_json = new JSONObject();
			result_string = "删除失败";
			result_json.put("return_status", "error");
			result_json.put("return_describe", result_string);
			if(check_result == null) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from recording_check_result where id=" + check_result.getId();
			Object obj = daos.select_object(system_session, hql, new recording_check_result());
			if(obj != null) {
				recording_check_result bean = (recording_check_result)obj;
				int re_id = bean.getRecording_id();
				hql = "from commons_records_list where id="+re_id;
				obj = daos.select_object(system_session, hql, new commons_records_list());
				if(obj != null) {
					//删除的同时解锁录音质检状态
					commons_records_list records = (commons_records_list)obj;
					records.setIs_check("");
					records.setCheck_user_id("");
					log.info("exec recording status result : 解锁成功");
					system_session.update(records);
				}
				
				system_session.delete(bean);
				result_string = "删除成功";
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
	
	//质检用户列表
	public String list_check_admin_user() {
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			/*hql = "select au.id,au.admin_user_name from admin_user au" +
				" inner join admin_user_authority auth" +
				" on au.id = auth.admin_user_id" +
				" inner join admin_module_group mg" +
				" on auth.admin_module_group_id = mg.id" +
				" where mg.module_group_name like '%录音质检员工组'";*/
			
			hql = "select au.id,au.admin_user_name from admin_user au" +
				" inner join admin_user_authority auth" +
				" on au.id = auth.admin_user_id" +
				" inner join admin_module_group mg" +
				" on auth.admin_module_group_id = mg.id" +
				" inner join admin_module_group_describe gd" +
				" on mg.id = gd.admin_module_group_id" +
				" inner join admin_module am" +
				" on gd.admin_module_id = am.id" +
				" where am.module_uri = '/ccdms/saveRecordingCheckResult.do'";
			list = daos.select_list(system_session, hql);
			
			List<Object> bean_list = new ArrayList<Object>();
			admin_user user_bean = null;
			if(list != null && list.size() > 0) {
				for(int i=0; i<list.size(); i++) {
					Object obj = list.get(i);
					Object[] objs = (Object[])obj;
					
					user_bean = new admin_user();
					user_bean.setId(Integer.parseInt(String.valueOf(objs[0])));
					user_bean.setAdmin_user_name(String.valueOf(objs[1]));
					bean_list.add(user_bean);
				}
			}
			user_bean = new admin_user();
			user_bean.setId(-1);
			user_bean.setAdmin_user_name("所有用户");
			bean_list.add(user_bean);
			HibernatePageJson json = new HibernatePageJson();
			json.setRows(bean_list);
			result_json = JSONObject.fromObject(json);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//修改录音质检状态
	public String modify_check_status() {
		log.info("Entry manage_recording_check[ modify_check_status()]");
		try {
			result_json = new JSONObject();
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from commons_records_list where is_check is null and id="+recording_id;
			log.info("exec hql : " + hql);
			Object obj = daos.select_object(system_session, hql, new commons_records_list());
			if(obj != null) {
				commons_records_list bean = (commons_records_list)obj;
				http_session = tools.get_http_session();
				user = (admin_user) http_session.getAttribute("ADMINUSER");
				bean.setIs_check("1");
				bean.setCheck_user_id(user.getId()+"");
				system_session.update(bean);
				
				recording_check_result check_result = new recording_check_result();
				check_result.setAgent_id(bean.getAgent_code());                    //工号
				check_result.setRecording_time(bean.getStart_time());              //录音时间
				check_result.setOrigcaller(bean.getOrig_caller_number());          //源主叫
				check_result.setOrigcalled(bean.getOrig_called_number());          //源被叫
				check_result.setCaller(bean.getCaller_number());                   //主叫
				check_result.setCalled(bean.getCalled_number());                   //被叫
				check_result.setCheck_time(common_util.dateFormatDTS(new Date())); //抽检时间
				check_result.setRecording_id(bean.getId());                        //录音ID
				check_result.setFile_name(bean.getFile_name());                    //文件路径
				
				http_session = tools.get_http_session();
				user = (admin_user) http_session.getAttribute("ADMINUSER");
				check_result.setCheck_user_id(user.getId());                       //质检用户
				system_session.save(check_result);
				
				result_string = "修改录音质检状态成功";
				result_json.put("return_status", "success");
				result_json.put("check_result_id", check_result.getId());
				result_json.put("return_describe", result_string);
			}else {
				result_string = "修改录音质检状态失败";
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
	
	//复检质检权限
	public String check_and_recheck() {
		log.info("Entry manage_recording_check[ check_and_recheck()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			http_session = tools.get_http_session();
			user = (admin_user) http_session.getAttribute("ADMINUSER");
			
			boolean is_check = false;
			boolean is_recheck = false;
			
			//录音质检员工组
			/*hql = "select auth.id from admin_user_authority auth" +
				" inner join admin_module_group mg" +
				" on auth.admin_module_group_id=mg.id" +
				" where mg.module_group_name like '%录音质检员工组' and auth.admin_user_id="+user.getId();*/
			
			hql = "select au.id from admin_user au" +
				" inner join admin_user_authority auth" +
				" on au.id = auth.admin_user_id" +
				" inner join admin_module_group mg" +
				" on auth.admin_module_group_id = mg.id" +
				" inner join admin_module_group_describe gd" +
				" on mg.id = gd.admin_module_group_id" +
				" inner join admin_module am" +
				" on gd.admin_module_id = am.id" +
				" where am.module_uri = '/ccdms/saveRecordingCheckResult.do' and auth.admin_user_id="+user.getId();
			
			list = daos.select_list(system_session, hql);
			if(list != null && list.size() > 0) {
				is_check = true;
			}
			//录音质检管理组
			/*hql = "select auth.id from admin_user_authority auth" +
				" inner join admin_module_group mg" +
				" on auth.admin_module_group_id=mg.id" +
				" where mg.module_group_name like '%录音质检管理组' and auth.admin_user_id="+user.getId();*/
			
			hql = "select auth.id from admin_user_authority auth" +
				" inner join admin_module_group mg" +
				" on auth.admin_module_group_id = mg.id" +
				" inner join admin_module_group_describe gd" +
				" on mg.id = gd.admin_module_group_id" +
				" inner join admin_module am" +
				" on gd.admin_module_id = am.id" +
				" where am.module_uri = '/ccdms/execRecheckResult.do' and auth.admin_user_id="+user.getId();
			list = daos.select_list(system_session, hql);
			if(list != null && list.size() > 0) {
				is_recheck = true;
			}
			
			result_json = new JSONObject();
			result_json.put("return_status", "success");
			result_json.put("is_check", is_check);
			result_json.put("is_recheck", is_recheck);
			result_json.put("admin_user_id", user.getId());
			log.info("exec result : is_check="+ is_check +", is_recheck="+ is_recheck);
		}catch(Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		}finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//导出EXCEL
	public void download_check_result() {
		log.info("Entry manage_recording_check[ download_check_result()]");
		try {
			result_string = "导出失败";
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from recording_check_result where 1=1";
			http_session = tools.get_http_session();
			user = (admin_user) http_session.getAttribute("ADMINUSER");

			//查询条件
			if(check_result != null) {
				
				//录音类型
				if(!common_util.isEmpty(check_result.getRecording_type())) {
					String temp = URLDecoder.decode(check_result.getRecording_type(), "UTF-8");
					hql += " and recording_type = '"+ temp +"'";
				}
				//质检用户
				if(check_user_id != null) {
					if(check_user_id != -1) {
						hql += " and check_user_id="+check_user_id;
					}
				}else {
					//只能看到自己的质检结果
					hql += " and check_user_id="+user.getId();
				}
				//抽检时间
				if(!common_util.isEmpty(start_time)) {
					String time_sql = " and check_time >= '"+ start_time +"'";
					if(!common_util.isEmpty(end_time)) {
						time_sql = " and check_time between '"+ start_time +"' and '"+ end_time +"'";
					}
					hql += time_sql;
				}
				//业务范围
				if(!common_util.isEmpty(check_result.getCall_type())) {
					String temp = URLDecoder.decode(check_result.getCall_type(), "UTF-8");
					hql += " and call_type='"+ temp +"'";
				}
				//错误类型
				if(!common_util.isEmpty(check_result.getError_type())) {
					String temp = URLDecoder.decode(check_result.getError_type(), "UTF-8");
					hql += " and error_type='"+ temp +"'";
				}
				//评分
				if(start_result != null && start_result != 0) {
					hql += " and check_result>="+start_result;
				}
				if(end_result != null && end_result != 0) {
					hql += " and check_result<="+end_result;
				}
				//复检
				if(is_check != null) {
					hql += (is_check == 1 ? (" and recheck_result is not null") : (" and recheck_result is null"));
				}
			}else {
				//只能看到自己的质检结果
				hql += " and check_user_id="+user.getId();
			}
			
			hql += " order by id desc";
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(system_session, hql);
			if(check_result != null) {
				String temp = URLDecoder.decode(check_result.getCall_type(), "UTF-8");
				if("江西彩铃".equals(temp)) {
					jiangxi_data(objList);//江西质检
				}else if("人保外呼".equals(temp)) {
					
					//用户账号信息
					Map<Integer, String> user_map = new HashMap<Integer, String>();
					hql = "select id,admin_user_name from admin_user";
					list = daos.select_list(system_session, hql);
					if(list != null) {
						for(int i=0; i<list.size(); i++) {
							Object obj = list.get(i);
							Object[] objs = (Object[])obj;
							int key = Integer.parseInt(String.valueOf(objs[0]));
							String value = String.valueOf(objs[1]);
							user_map.put(key, value);
						}
					}
					
					renbao_data(objList, user_map);//人保质检
				}else if("海普外呼".equals(temp)) {
					haipu_data(objList);//海普质检
				}else {
					other_data(objList);
				}
			}
			
			result_string = "导出成功";
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		
		log.info("exec result : " + result_string);
	}
	
	
	//导出EXCEL(所有)
	public void other_data(List<Object> objList) {
		log.info("download all excel");
		try {
			//EXCEL
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
			
			Calendar now = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = format.format(now.getTime());
			
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流  
	        response.setHeader("Content-disposition", "attachment; filename="+fileName+".xls");// 设定输出文件头  
	        response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型 
			
			//添加工作表
	        WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet("质检结果", 0);
			
			//表头
			String[] titles = new String[]{"序号", "工号", "姓名", "团队", "录音时间",
				"抽检时间", "源主叫", "主叫", "源被叫", "被叫", "业务范围", 
				"业务类型", "差错等级", "考核项", "评分", "质检评语", "复检评语", "备注"};
			
			Label label = null;
			for(int i=0; i<titles.length; i++) {
				label = new jxl.write.Label(i, 0, titles[i]);
				sheet.addCell(label);
			}
			
			if(objList != null && objList.size() > 0) {
				
				//数据
				for(int i=0; i<objList.size(); i++) {
					recording_check_result check_result = (recording_check_result)objList.get(i);
					
					label = new jxl.write.Label(0, (i+1), String.valueOf(i+1));
					sheet.addCell(label);
					label = new jxl.write.Label(1, (i+1), check_result.getAgent_id());
					sheet.addCell(label);
					label = new jxl.write.Label(2, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(3, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(4, (i+1), check_result.getRecording_time());
					sheet.addCell(label);
					label = new jxl.write.Label(5, (i+1), check_result.getCheck_time());
					sheet.addCell(label);
					
					label = new jxl.write.Label(6, (i+1), check_result.getOrigcaller());
					sheet.addCell(label);
					label = new jxl.write.Label(7, (i+1), check_result.getCaller());
					sheet.addCell(label);
					label = new jxl.write.Label(8, (i+1), check_result.getOrigcalled());
					sheet.addCell(label);
					label = new jxl.write.Label(9, (i+1), check_result.getCalled());
					sheet.addCell(label);
					
					label = new jxl.write.Label(10, (i+1), check_result.getCall_type());
					sheet.addCell(label);
					label = new jxl.write.Label(11, (i+1), check_result.getCall_leaf_type());
					sheet.addCell(label);
					label = new jxl.write.Label(12, (i+1), check_result.getError_type());
					sheet.addCell(label);
					label = new jxl.write.Label(13, (i+1), check_result.getCheck_type());
					sheet.addCell(label);
					label = new jxl.write.Label(14, (i+1), check_result.getCheck_result()+"");
					sheet.addCell(label);
					label = new jxl.write.Label(15, (i+1), check_result.getResult_describe());
					sheet.addCell(label);
					label = new jxl.write.Label(16, (i+1), check_result.getRecheck_result());
					sheet.addCell(label);
					label = new jxl.write.Label(17, (i+1), "");
					sheet.addCell(label);
				}
			}
			
			workbook.write();
			workbook.close();
			os.close();
			result_string = "导出成功";
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//导出EXCEL(江西)
	public void jiangxi_data(List<Object> objList) {
		log.info("download jiangxi excel");
		try {
			//EXCEL
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
			
			Calendar now = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = format.format(now.getTime());
			
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流  
	        response.setHeader("Content-disposition", "attachment; filename="+fileName+".xls");// 设定输出文件头  
	        response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型 
			
			//添加工作表
	        WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet("江西质检结果", 0);
			
			//表头
			String[] titles = new String[]{"序号", "工号", "姓名", "团队", "录音时间",
				"抽检时间", "业务流水号","业务类型", "差错等级", "考核项", "评分", 
				"质检评语", "复检评语", "备注", "业务范围"};
			
			Label label = null;
			for(int i=0; i<titles.length; i++) {
				label = new jxl.write.Label(i, 0, titles[i]);
				sheet.addCell(label);
			}
			
			if(objList != null && objList.size() > 0) {
				
				//数据
				for(int i=0; i<objList.size(); i++) {
					recording_check_result check_result = (recording_check_result)objList.get(i);
					
					label = new jxl.write.Label(0, (i+1), String.valueOf(i+1));
					sheet.addCell(label);
					label = new jxl.write.Label(1, (i+1), check_result.getAgent_id());
					sheet.addCell(label);
					label = new jxl.write.Label(2, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(3, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(4, (i+1), check_result.getRecording_time());
					sheet.addCell(label);
					label = new jxl.write.Label(5, (i+1), check_result.getCheck_time());
					sheet.addCell(label);
					label = new jxl.write.Label(6, (i+1), check_result.getOrigcaller());//业务流水号
					sheet.addCell(label);
					label = new jxl.write.Label(7, (i+1), check_result.getCall_leaf_type());
					sheet.addCell(label);
					label = new jxl.write.Label(8, (i+1), check_result.getError_type());
					sheet.addCell(label);
					label = new jxl.write.Label(9, (i+1), check_result.getCheck_type());
					sheet.addCell(label);
					label = new jxl.write.Label(10, (i+1), check_result.getCheck_result()+"");
					sheet.addCell(label);
					label = new jxl.write.Label(11, (i+1), check_result.getResult_describe());
					sheet.addCell(label);
					label = new jxl.write.Label(12, (i+1), check_result.getRecheck_result());
					sheet.addCell(label);
					label = new jxl.write.Label(13, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(14, (i+1), check_result.getCall_type());
					sheet.addCell(label);
				}
			}
			
			workbook.write();
			workbook.close();
			os.close();
			result_string = "导出成功";
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//导出EXCEL(人保)
	public void renbao_data(List<Object> objList, Map<Integer, String> user_map) {
		log.info("download renbao excel");
		try {
			//EXCEL
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
			
			Calendar now = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = format.format(now.getTime());
			
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流  
	        response.setHeader("Content-disposition", "attachment; filename="+fileName+".xls");// 设定输出文件头  
	        response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型 
			
			//添加工作表
	        WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet("江西质检结果", 0);
			
			//表头
			String[] titles = new String[]{"序号", "工号", "姓名", "班组", "录音时间",
				"抽检时间", "保单号", "业务流水号", "业务类型", "差错等级", "考核项", "评分", 
				"质检评语", "复检评语", "备注", "回访结果", "业务范围"};
			
			Label label = null;
			for(int i=0; i<titles.length; i++) {
				label = new jxl.write.Label(i, 0, titles[i]);
				sheet.addCell(label);
			}
			
			if(objList != null && objList.size() > 0) {
				
				//数据
				for(int i=0; i<objList.size(); i++) {
					recording_check_result check_result = (recording_check_result)objList.get(i);
					
					label = new jxl.write.Label(0, (i+1), String.valueOf(i+1));
					sheet.addCell(label);
					label = new jxl.write.Label(1, (i+1), check_result.getAgent_id());
					sheet.addCell(label);
					label = new jxl.write.Label(2, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(3, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(4, (i+1), check_result.getRecording_time());
					sheet.addCell(label);
					label = new jxl.write.Label(5, (i+1), check_result.getCheck_time());
					sheet.addCell(label);
					label = new jxl.write.Label(6, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(7, (i+1), check_result.getCalled());//业务流水号
					sheet.addCell(label);
					label = new jxl.write.Label(8, (i+1), check_result.getCall_leaf_type());
					sheet.addCell(label);
					label = new jxl.write.Label(9, (i+1), check_result.getError_type());
					sheet.addCell(label);
					label = new jxl.write.Label(10, (i+1), check_result.getCheck_type());
					sheet.addCell(label);
					label = new jxl.write.Label(11, (i+1), check_result.getCheck_result()+"");
					sheet.addCell(label);
					label = new jxl.write.Label(12, (i+1), check_result.getResult_describe());
					sheet.addCell(label);
					label = new jxl.write.Label(13, (i+1), check_result.getRecheck_result());
					sheet.addCell(label);
					label = new jxl.write.Label(14, (i+1), user_map.get(check_result.getCheck_user_id()));//备注
					sheet.addCell(label);
					label = new jxl.write.Label(15, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(16, (i+1), check_result.getCall_type());
					sheet.addCell(label);
				}
			}
			
			workbook.write();
			workbook.close();
			os.close();
			result_string = "导出成功";
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//导出EXCEL(海普外呼)
	public void haipu_data(List<Object> objList) {
		log.info("download haipu excel");
		try {
			//EXCEL
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
			
			Calendar now = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = format.format(now.getTime());
			
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流  
	        response.setHeader("Content-disposition", "attachment; filename="+fileName+".xls");// 设定输出文件头  
	        response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型 
			
			//添加工作表
	        WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet("江西质检结果", 0);
			
			//表头
			String[] titles = new String[]{"序号", "工号", "姓名", "团队", "录音时间",
				"抽检时间", "业务流水号", "业务类型", "差错等级", "考核项", "评分", 
				"质检评语", "复检评语", "备注", "业务范围"};
			
			Label label = null;
			for(int i=0; i<titles.length; i++) {
				label = new jxl.write.Label(i, 0, titles[i]);
				sheet.addCell(label);
			}
			
			if(objList != null && objList.size() > 0) {
				
				//数据
				for(int i=0; i<objList.size(); i++) {
					recording_check_result check_result = (recording_check_result)objList.get(i);
					
					label = new jxl.write.Label(0, (i+1), String.valueOf(i+1));
					sheet.addCell(label);
					label = new jxl.write.Label(1, (i+1), check_result.getAgent_id());
					sheet.addCell(label);
					label = new jxl.write.Label(2, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(3, (i+1), "");
					sheet.addCell(label);
					label = new jxl.write.Label(4, (i+1), check_result.getRecording_time());
					sheet.addCell(label);
					label = new jxl.write.Label(5, (i+1), check_result.getCheck_time());
					sheet.addCell(label);
					
					//去996
					if(check_result.getCaller() != null && check_result.getCaller().indexOf("996") == 0) {
						check_result.setCaller(check_result.getCaller().substring(3));
					}
					label = new jxl.write.Label(6, (i+1), check_result.getCaller());//业务流水号
					sheet.addCell(label);
					label = new jxl.write.Label(7, (i+1), check_result.getCall_leaf_type());
					sheet.addCell(label);
					label = new jxl.write.Label(8, (i+1), check_result.getError_type());
					sheet.addCell(label);
					label = new jxl.write.Label(9, (i+1), check_result.getCheck_type());
					sheet.addCell(label);
					label = new jxl.write.Label(10, (i+1), check_result.getCheck_result()+"");
					sheet.addCell(label);
					label = new jxl.write.Label(11, (i+1), check_result.getResult_describe());
					sheet.addCell(label);
					label = new jxl.write.Label(12, (i+1), check_result.getRecheck_result());
					sheet.addCell(label);
					label = new jxl.write.Label(13, (i+1), "");//备注
					sheet.addCell(label);
					label = new jxl.write.Label(14, (i+1), check_result.getCall_type());
					sheet.addCell(label);
				}
			}
			
			workbook.write();
			workbook.close();
			os.close();
			result_string = "导出成功";
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public recording_check_result getCheck_result() {
		return check_result;
	}

	public void setCheck_result(recording_check_result check_result) {
		this.check_result = check_result;
	}

	public Integer getRecording_id() {
		return recording_id;
	}

	public void setRecording_id(Integer recording_id) {
		this.recording_id = recording_id;
	}

	public commons_records_list getRecords_list() {
		return records_list;
	}

	public void setRecords_list(commons_records_list records_list) {
		this.records_list = records_list;
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

	public Integer getStart_result() {
		return start_result;
	}

	public void setStart_result(Integer start_result) {
		this.start_result = start_result;
	}

	public Integer getEnd_result() {
		return end_result;
	}

	public void setEnd_result(Integer end_result) {
		this.end_result = end_result;
	}

	public Integer getIs_check() {
		return is_check;
	}

	public void setIs_check(Integer is_check) {
		this.is_check = is_check;
	}

	public Integer getCheck_user_id() {
		return check_user_id;
	}

	public void setCheck_user_id(Integer check_user_id) {
		this.check_user_id = check_user_id;
	}

	public String getCall_type() {
		return call_type;
	}

	public void setCall_type(String call_type) {
		this.call_type = call_type;
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
	
}

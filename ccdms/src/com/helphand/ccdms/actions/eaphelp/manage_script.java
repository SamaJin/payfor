package com.helphand.ccdms.actions.eaphelp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.channel_param_bean;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.commons.common_util;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.tables.eaphelp.callout_result;
import com.helphand.ccdms.tables.eaphelp.callout_script;
import com.helphand.ccdms.tables.eaphelp.callout_script_detail;
import com.helphand.ccdms.tables.eaphelp.callout_script_options;
import com.helphand.ccdms.tables.eaphelp.user_information;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 脚本管理
 */
public class manage_script extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_script.class);
	private common_tools tools = new common_tools();
	private HibernateDAO daos = new HibernateDAO();
	private Session dbs_session = null;
	private Transaction dbs_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private String hql = null;
	private boolean check = true;

	private int limit, page;
	private JSONObject result_json = null;
	private String result_string = null;
	private callout_script script = null;
	private callout_script_detail script_detail = null;
	private channel_param_bean channel_bean = null;
	private String result_id = null;
	private String list_result = null;
	
	private String search_script_name = null;
	private String search_start_time = null;
	private String search_end_time = null;
	
	public String init_script_list() {
		log.info("Entry manage_script[ init_script_list()]");
		return SUCCESS;
	}

	//保存脚本
	public String add_script() {
		log.info("Entry manage_script[ add_script()]");
		try {
			result_json = new JSONObject();
			script = (script == null ? new callout_script() : script);
			
			//脚本名称
			if(tools.isEmpty(script.getScript_name())) {
				result_string = "脚本名称不能为空";
				result_json.put("resp", "error");
				result_json.put("describe", result_string);
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();

			String create_time = tools.dateFormatDTS(new Date());
			script.setCreate_time(create_time);
			if(script.getId() == 0) {
				script.setScript_name(script.getScript_name() + "_" + create_time.substring(0,10));
			}
			dbs_session.saveOrUpdate(script);
			
			hql = "delete from callout_script_options where script_id="+script.getId();
			daos.execute_sql(dbs_session, hql);
			callout_script_options options = new callout_script_options();
			options.setScript_id(script.getId());
			options.setDescribe(list_result);
			dbs_session.save(options);
		} catch (Exception e) {
			result_json.put("resp", "error");
			result_string = "保存失败";
			result_json.put("describe", result_string);
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			try {
				dbs_transaction.commit();
				result_json.put("resp", "success");
				result_string = "保存成功";
			} catch (Exception e) {
				result_json.put("resp", "error");
				result_string = "脚本名称重复";
				result_json.put("describe", result_string);
			}
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//获取脚本执行结果
	public String get_edit_script_options() {
		log.info("Entry manage_script[ get_edit_script_options() ]");
		try {
			script = (script == null ? new callout_script() : script);
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script_options where script_id="+script.getId();
			list = daos.select_list(dbs_session, hql);
			if(list != null && list.size() > 0) {
				callout_script_options options = (callout_script_options)list.get(0);
				result_string = options.getDescribe();
			}
			
			result_json = new JSONObject();
			result_json.put("resp", "success");
			result_json.put("describe", result_string);
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//查询脚本
	public String list_script() {
		log.info("Entry manage_script[ list_script()]");
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script where 1=1";
			if(!common_util.isEmpty(search_script_name)) {
				hql += " and script_name like '%"+ search_script_name +"%'";
			}
			if(!common_util.isEmpty(search_start_time)) {
				String temp_sql = " and create_time >= '"+ search_start_time +" 00:00:00'";
				if(!common_util.isEmpty(search_end_time)) {
					temp_sql = " and create_time between '"+ search_start_time +" 00:00:00' and '"+ search_end_time +" 23:59:59'";
				}
				hql += temp_sql;
			}
			hql += " order by create_time desc";
			script = new callout_script();
			log.info("exec hql : " + hql);
			page_object = daos.select_pages(dbs_session, hql, page, limit, script);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			list = list == null ? new ArrayList<Object>() : list;
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
	
	//查询所有脚本
	public String all_script() {
		log.info("Entry manage_script[ all_script()]");
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script where issue=1 order by create_time desc";
			
			script = new callout_script();
			log.info("exec hql : " + hql);
			list = daos.select_list(dbs_session, hql);
			list = list == null ? new ArrayList<Object>() : list;
			page_json = new HibernatePageJson();
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
	
	//删除脚本
	public String del_script() {
		log.info("Entry manage_script[ del_script()]");
		try {
			result_json = new JSONObject();
			script = (script == null ? new callout_script() : script);
			result_string = "脚本删除失败";
			result_json.put("resp", "error");
			result_json.put("describe", result_string);
			
			//ID为空
			if(script.getId() == 0) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script where id=" + script.getId();
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(dbs_session, hql);
			if(objList != null && objList.size() > 0) {
				callout_script obj_script = (callout_script)objList.get(0);
				if(obj_script.getIssue() == 1) {
					result_string = "脚本已经发布，不能删除";
					log.info("exec result : " + result_string);
					result_json.put("describe", result_string);
					return SUCCESS;
				}
				
				//删除脚本对应的结果
				hql = "delete from callout_script_options where script_id="+obj_script.getId();
				daos.execute_sql(dbs_session, hql);
				dbs_session.delete(obj_script);
				result_string = "脚本删除成功";
				result_json.put("resp", "success");
			}

			log.info("exec result : " + result_string);
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//发布
	public String exec_script() {
		log.info("Entry manage_script[ exec_script()]");
		try {
			result_json = new JSONObject();
			script = (script == null ? new callout_script() : script);
			result_string = "发布失败";
			result_json.put("resp", "error");
			result_json.put("describe", result_string);
			
			if(script.getId() == 0 || script.getIssue() == 0) {
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script where id=" + script.getId();
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(dbs_session, hql);
			if(objList != null && objList.size() > 0) {
				callout_script obj_script = (callout_script)objList.get(0);
				obj_script.setIssue(script.getIssue());
				dbs_session.update(obj_script);
				result_string = "发布成功";
				result_json.put("resp", "success");
			}
			log.info("exec result : " + result_string);
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//查询脚本详细
	public String info_script_detail() {
		log.info("Entry manage_script[ info_script_detail()]");
		try {
			result_json = new JSONObject();
			script_detail = (script_detail == null ? new callout_script_detail() : script_detail);
			if(script_detail.getScript_id() == 0) {
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script_detail where script_id=" + script_detail.getScript_id();
			hql += " order by content_sort";
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(dbs_session, hql);
			if(objList != null && objList.size() > 0) {
				result_json.put("list", objList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//查询脚本单项详细
	public String info_script_radio_detail() {
		log.info("Entry manage_script[ info_script_radio_detail()]");
		try {
			result_json = new JSONObject();
			script_detail = (script_detail == null ? new callout_script_detail() : script_detail);
			if(script_detail.getId() == 0) {
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script_detail where id=" + script_detail.getId();
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(dbs_session, hql);
			if(objList != null && objList.size() > 0) {
				result_json.put("resp", "success");
				result_json.put("script_detail_bean", objList.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//添加脚本
	public String add_script_detail() {
		log.info("Entry manage_script[ add_script_detail()]");
		try {
			result_json = new JSONObject();
			script_detail = (script_detail == null ? new callout_script_detail() : script_detail);
			if(script_detail.getContent_sort() <= 0) {
				result_string = "排序不合法";
				check = false;
			}
			else if(tools.isEmpty(script_detail.getContent_type())) {
				result_string = "类型不能为空";
				check = false;
			}
			else if(tools.isEmpty(script_detail.getContent_detail())) {
				result_string = "内容详情不能为空";
				check = false;
			}
			
			if(!check) {
				result_json.put("resp", "error");
				result_json.put("describe", result_string);
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			dbs_session.saveOrUpdate(script_detail);
			result_json.put("resp", "success");
			result_string = "脚本保存成功";
			log.info("exec result : " + result_string);
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//删除脚本内容信息
	public String del_script_detail() {
		log.info("Entry manage_script[ del_script_detail()]");
		try {
			result_json = new JSONObject();
			script_detail = (script_detail == null ? new callout_script_detail() : script_detail);
			if(script_detail.getId() == 0) {
				result_string = "删除失败";
				result_json.put("resp", "error");
				result_json.put("describe", result_string);
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_script_detail where id=" + script_detail.getId();
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(dbs_session, hql);
			if(objList != null && objList.size() > 0) {
				callout_script_detail obj_script_detail = (callout_script_detail)objList.get(0);
				dbs_session.delete(obj_script_detail);
				result_string = "删除成功";
				result_json.put("resp", "success");
				log.info("exec result : " + result_string);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//显示脚本
	public String show_script_detail_info() {
		log.info("Entry manage_script[ show_script_detail_info()]");
		try {
			result_json = new JSONObject();
			script_detail = (script_detail == null ? new callout_script_detail() : script_detail);
			channel_bean = (channel_bean == null ? new channel_param_bean() : channel_bean);
			result_json.put("resp", "error");
			result_json.put("describe", "获取信息失败");
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			
			String head_script = "select s.id as script_id,s.script_name,sd.content_type,sd.content_detail";
			String head_channel = "";
			hql = " from callout_script s" +
				" inner join callout_script_detail sd" +
				" on s.id = sd.script_id";
			
			if(script_detail.getScript_id() == 0) {
				
				//通道业务
				if(!tools.isEmpty(channel_bean.getAgent_id())) {
					head_channel = ",ch.channel_name,ch.id as channel_id";
					hql += " inner join callout_channel ch" +
						" on ch.script_id = sd.script_id" + 
						" where ch.issue=1 and ch.channel_status=1";
					
					if(!tools.isEmpty(channel_bean.getCalled())) {
						hql += " and ch.called='"+ channel_bean.getCalled() +"'";
					}
				}else {
					return SUCCESS;
				}
			}else {
				hql += " where sd.script_id="+script_detail.getScript_id();
			}

			
			hql += " order by sd.content_sort";
			hql = head_script + head_channel + hql;
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(dbs_session, hql);

			String script_id = null;
			String script_name = null;
			String content_type = null;
			String content_detail = null;
			String channel_name = "";
			//String html = "<form name='form_result' method='post' action=''>";
			String user_info = "";
			hql = "from user_information where mobile='"+ channel_bean.getOrigcalled() +"'";
			List<Object> info_list = daos.select_list(dbs_session, hql);
			user_info = ( (info_list == null || info_list.size() == 0) ? "" : ( (user_information)info_list.get(0) ).getColumn1() );
			String html = "<b>详细：" + user_info + "</b></br></br>";
			String[] contents, opts;
			if(objList != null && objList.size() > 0) {
				int index = 0;//序号
				for(int i=0; i<objList.size(); i++) {
					Object[] obj = (Object[])objList.get(i);
					script_id = String.valueOf(obj[0]);
					script_name = String.valueOf(obj[1]);
					content_type = String.valueOf(obj[2]);
					content_detail = String.valueOf(obj[3]);
					content_detail = content_detail.replaceAll("\\n", "</BR>");//换行符替换成<BR>
					
					//通道名称、通道ID
					if(i == 0 && obj.length > 5) {
						channel_name = String.valueOf(obj[4]);
						String channel_id = String.valueOf(obj[5]);
						
						html += "<input type='hidden' id='script_name' value='"+ script_name +"'/>";
						html += "<input type='hidden' id='script_id' value='"+ script_id +"'/>";
						html += "<input type='hidden' id='channel_name' value='"+ channel_name +"'/>";
						html += "<input type='hidden' id='channel_id' value='"+ channel_id +"'/>";
					}
					
					if("describe".equals(content_type)) {
						html += "<input type='hidden' name='qn"+ (i + 1) +"'/>"+ (++index +"、") + content_detail + "<br>";
					}
					else if("radio".equals(content_type)) {
						contents = content_detail.split("//");
						html += (++index +"、") + contents[0] + "<br>";
						if(contents.length >= 2) {
							opts = StringUtils.split(contents[1], "||");
							opts = opts == null ? new String[0] : opts;
							for (int j = 0; j < opts.length; j++) {
								html += "<input type='radio' name='qn" + (i + 1) + "' value='" + (j + 1) + "'>" + opts[j] + "&nbsp;&nbsp;";
							}
							html += "<br>";
						}
					}
					else if("checkbox".equals(content_type)) {
						contents = content_detail.split("//");
						html += (++index +"、") + contents[0] + "<br>";
						if(contents.length >= 2) {
							opts = StringUtils.split(contents[1], "||");
							opts = opts == null ? new String[0] : opts;
							for (int j = 0; j < opts.length; j++) {
								html += "<input type='checkbox' name='qn" + (i + 1) + "' value='" + (j + 1) + "'>" + opts[j] + "&nbsp;&nbsp;";
							}
							html += "<br>";
						}
					}
					else if("question".equals(content_type)) {
						html += (++index +"、") + content_detail + "<br>";
						html += "<input type='text' name='qn" + (i + 1) + "' id='q" + (i + 1) + "'>";
						html += "<br>";
					}
					html += "<br>";
				}
				
				hql = "select describe from callout_script_options where script_id="+script_id;
				objList = daos.select_list(dbs_session, hql);
				if(objList != null && objList.size() > 0) {
					String describe = String.valueOf(objList.get(0));
					contents = StringUtils.split(describe, "||");
					String options = "";
					String seleted = "";
					for (int i = 0; i < contents.length; i++) {
						seleted = (i == 0 ? "seleted" : "");
						options += "<option value='"+ contents[i] +"' "+seleted+">"+ contents[i] +"</option>";
					}
					html += (++index +"、") + "执行结果：<select name='option_result'>"+ options +"</select>";
				}
				
				result_json.put("resp", "success");
				result_json.put("script_name", script_name);
				result_json.put("channel_name", channel_name);
				result_json.put("html", html);
			}
			//html += "</form>";
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//显示需要修改的表单
	public String show_script_data_info() {
		log.info("Entry manage_script[ show_script_data_info()]");
		try {
			result_json = new JSONObject();
			result_json.put("resp", "error");
			result_json.put("describe", "获取信息失败");
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			
			if(tools.isEmpty(result_id)) {
				return SUCCESS;
			}
			
			//需要修改的数据
			hql = "from callout_result where id="+result_id;
			log.info("exec hql : " + hql);
			Object obj_result = daos.select_object(dbs_session, hql, new callout_result());
			if(obj_result == null) {
				return SUCCESS;
			}
			callout_result result = (callout_result)obj_result;
			
			//脚本信息
			hql = "from callout_script_detail where script_id="+result.getScript_id();
			hql += " order by content_sort asc";
			log.info("exec hql : " + hql);
			List<Object> objList = daos.select_list(dbs_session, hql);

			String user_info = "";
			hql = "from user_information where mobile='"+ channel_bean.getOrigcalled() +"'";
			List<Object> info_list = daos.select_list(dbs_session, hql);
			user_info = ( (info_list == null || info_list.size() == 0) ? "" : ( (user_information)info_list.get(0) ).getColumn1() );
			String html = "详细：" + user_info + "</br></br>";
			String[] contents, opts;
			
			if(objList != null && objList.size() > 0) {
				
				//;;;1;2|3;describe;;;
				String result_describe = result.getResult_describe();
				String[] describe = result_describe.split(";");
				int index = 0;
				for(int i=0; i<objList.size(); i++) {
					callout_script_detail script_detail = (callout_script_detail)objList.get(i);
					String content_type = script_detail.getContent_type();
					String content_detail = script_detail.getContent_detail();
					
					if("describe".equals(content_type)) {
						html += "<input type='hidden' name='qn"+ (i + 1) +"'/>" +(++index +"、") +content_detail + "<br>";
					}
					else if("radio".equals(content_type)) {
						contents = content_detail.split("//");
						html += (++index +"、") + contents[0] + "<br>";
						if(contents.length >= 2) {
							opts = StringUtils.split(contents[1], "||");
							opts = opts == null ? new String[0] : opts;
							
							int value = -1;
							if(describe.length != 0 && !"".equals(describe[i])) {
								value = Integer.parseInt(describe[i]);
							}
							
							for (int j = 0; j < opts.length; j++) {
								if((j+1) == value) {
									html += "<input type='radio' name='qn" + (i + 1) + "' value='" + (j + 1) + "' checked='true'>" + opts[j] + "&nbsp;&nbsp;";
								}else {
									html += "<input type='radio' name='qn" + (i + 1) + "' value='" + (j + 1) + "'>" + opts[j] + "&nbsp;&nbsp;";
								}
							}
							html += "<br>";
						}
					}
					else if("checkbox".equals(content_type)) {
						contents = content_detail.split("//");
						html += (++index +"、") + contents[0] + "<br>";
						if(contents.length >= 2) {
							opts = StringUtils.split(contents[1], "||");
							opts = opts == null ? new String[0] : opts;
							
							String value = "";
							if(describe.length != 0 && !"".equals(describe[i])) {
								value = describe[i];
							}
							
							String[] values = value.split("\\|");
							List<String> checkboxList = Arrays.asList(values);
							
							for (int j = 0; j < opts.length; j++) {
								if(checkboxList.contains((j+1)+"")) {
									html += "<input type='checkbox' name='qn" + (i + 1) + "' value='" + (j + 1) + "' checked='true'>" + opts[j] + "&nbsp;&nbsp;";
								}else {
									html += "<input type='checkbox' name='qn" + (i + 1) + "' value='" + (j + 1) + "'>" + opts[j] + "&nbsp;&nbsp;";
								}
							}
							html += "<br>";
						}
					}
					else if("question".equals(content_type)) {
						String value = "";
						if(describe.length != 0 && !"".equals(describe[i])) {
							value = describe[i];
						}
						html += (++index +"、") + content_detail + "<br>";
						html += "<input type='text' name='qn" + (i + 1) + "' id='q" + (i + 1) + "' value='"+ value +"'>";
						html += "<br>";
					}
										
					html += "<br>";
				}
				
				//执行结果
				callout_script_detail script_detail = (callout_script_detail)objList.get(0);
				hql = "select describe from callout_script_options where script_id="+script_detail.getScript_id();
				objList = daos.select_list(dbs_session, hql);
				if(objList != null && objList.size() > 0) {
					String des = String.valueOf(objList.get(0));
					contents = StringUtils.split(des, "||");
					String options = "";
					String selected = "";
					for (int num = 0; num < contents.length; num++) {
						selected = (contents[num].equals(describe[describe.length-1]) ? "selected" : "");
						options += "<option value='"+ contents[num] +"' "+selected+">"+ contents[num] +"</option>";
					}
					html += (++index +"、") + "执行结果：<select name='option_result'>"+ options +"</select>";
				}
			}
			
			result_json.put("resp", "success");
			result_json.put("callout_result", result);
			result_json.put("html", html);
			
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	public callout_script getScript() {
		return script;
	}

	public void setScript(callout_script script) {
		this.script = script;
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

	public callout_script_detail getScript_detail() {
		return script_detail;
	}

	public void setScript_detail(callout_script_detail script_detail) {
		this.script_detail = script_detail;
	}

	public channel_param_bean getChannel_bean() {
		return channel_bean;
	}

	public void setChannel_bean(channel_param_bean channel_bean) {
		this.channel_bean = channel_bean;
	}

	public String getResult_id() {
		return result_id;
	}

	public void setResult_id(String result_id) {
		this.result_id = result_id;
	}

	public String getList_result() {
		return list_result;
	}

	public void setList_result(String list_result) {
		this.list_result = list_result;
	}

	public String getSearch_script_name() {
		return search_script_name;
	}

	public void setSearch_script_name(String search_script_name) {
		this.search_script_name = search_script_name;
	}

	public String getSearch_start_time() {
		return search_start_time;
	}

	public void setSearch_start_time(String search_start_time) {
		this.search_start_time = search_start_time;
	}

	public String getSearch_end_time() {
		return search_end_time;
	}

	public void setSearch_end_time(String search_end_time) {
		this.search_end_time = search_end_time;
	}
	
}

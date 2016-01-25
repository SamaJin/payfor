package com.helphand.ccdms.actions.eaphelp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.channel_day_num_bean;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.commons.common_util;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_10;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_6;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.tables.eaphelp.callout_channel;
import com.helphand.ccdms.tables.eaphelp.callout_script_count_info;
import com.helphand.ccdms.tables.eaphelp.callout_script_html;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 通道管理
 */
public class manage_channel extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_channel.class);
	private common_tools tools = new common_tools();
	private HibernateDAO daos = new HibernateDAO();
	private Session dbs_session = null;
	private Transaction dbs_transaction = null;
	private HibernatePageDemo page_object = null;
	private HibernatePageJson page_json = null;
	private List<Object> list = null;
	private String hql = null;
	
	private int limit, page;
	private JSONObject result_json = null;
	private String result_string = null;
	private callout_channel channel = null;
	private int channel_type;
	private String start_time;
	private String end_time;
	
	public String init_channel_list() {
		log.info("Entry manage_channel[ init_channel_list()]");
		return SUCCESS;
	}
	public String init_channel_day_list() {
		log.info("Entry manage_channel[ init_channel_day_list()]");
		return SUCCESS;
	}

	//查询
	public String list_callout_channel() {
		log.info("Entry manage_channel[ list_callout_channel()]");
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_channel";
			
			channel = new callout_channel();
			log.info("exec hql : " + hql);
			page_object = daos.select_pages(dbs_session, hql, page, limit, channel);
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
	
	//保存通道信息
	public String save_callout_channel() {
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			
			result_json = new JSONObject();
			channel = (channel == null ? new callout_channel() : channel);
			String called_temp = null;
			if(channel.getId() > 0) {
				
				//修改
				hql = "from callout_channel where id="+channel.getId();
				log.info("exec hql : " + hql);
				list = daos.select_list(dbs_session, hql);
				if(list != null && list.size() > 0) {
					callout_channel obj_channel = (callout_channel)list.get(0);
					obj_channel.setScript_id(channel.getScript_id());
					dbs_session.update(obj_channel);
					dbs_session.flush();
					log.info("exec result : 修改通道信息");
					
					called_temp = obj_channel.getCalled();
				}else {
					log.info("exec result : 未查询到通道信息, id : " + channel.getId());
				}
			}else {
				
				//新增
				if(tools.isEmpty(channel.getChannel_name())) {
					result_string = "通道名称不能为空";
					result_json.put("resp", "error");
					result_json.put("describe", result_string);
					log.info("check result : " + result_string);
					return SUCCESS;
				}
				
				channel.setCreate_time(tools.dateFormatDTS(new Date()));
				dbs_session.save(channel);
				dbs_session.flush();
				log.info("exec result : 新增通道信息");
				
				called_temp = channel.getCalled();
			}
			
			//保存通道对应的脚本HTML
			script_save_html(dbs_session, called_temp);
			//保存通道对应的脚本统计
			script_save_count_info(dbs_session, called_temp);
			result_json.put("resp", "success");
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(dbs_transaction);
		} finally {
			daos.commit_transaction(dbs_transaction);
			daos.close_session(dbs_session);
		}
		return SUCCESS;
	}
	
	//删除通道信息
	public String del_callout_channel() {
		log.info("Entry manage_channel[ del_callout_channel()]");
		try {
			result_json = new JSONObject();
			channel = (channel == null ? new callout_channel() : channel);
			if(channel.getId() == 0) {
				result_string = "删除失败";
				result_json.put("resp", "error");
				result_json.put("describe", result_string);
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_channel where id="+channel.getId();
			log.info("exec hql : " + hql);
			list = daos.select_list(dbs_session, hql);
			if(list != null && list.size() > 0) {
				callout_channel obj = (callout_channel)list.get(0);
				dbs_session.delete(obj);
				result_string = "删除成功";
				log.info("exec result : " + result_string);
				result_json.put("resp", "success");
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
	
	//发布通道信息
	public String exec_callout_channel() {
		log.info("Entry manage_channel[ exec_callout_channel()]");
		try {
			result_json = new JSONObject();
			channel = (channel == null ? new callout_channel() : channel);
			if(channel.getId() == 0) {
				result_string = "发布失败";
				result_json.put("resp", "error");
				result_json.put("describe", result_string);
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_channel where id="+channel.getId();
			log.info("exec hql : " + hql);
			list = daos.select_list(dbs_session, hql);
			if(list != null && list.size() > 0) {
				callout_channel obj = (callout_channel)list.get(0);
				obj.setIssue(channel.getIssue());
				dbs_session.update(obj);
				result_string = "发布成功";
				log.info("exec result : " + result_string);
				result_json.put("resp", "success");
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
	
	//激活、暂停
	public String on_off_callout_channel() {
		log.info("Entry manage_channel[ on_off_callout_channel()]");
		try {
			result_json = new JSONObject();
			channel = (channel == null ? new callout_channel() : channel);
			if(channel.getId() == 0) {
				result_string = "操作失败";
				result_json.put("resp", "error");
				result_json.put("describe", result_string);
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_channel where id="+channel.getId();
			log.info("exec hql : " + hql);
			list = daos.select_list(dbs_session, hql);
			if(list != null && list.size() > 0) {
				callout_channel obj = (callout_channel)list.get(0);
				obj.setChannel_status(channel.getChannel_status());
				dbs_session.update(obj);
				result_string = "操作成功";
				log.info("exec result : " + result_string);
				result_json.put("resp", "success");
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
	
	//html
	public void script_save_html(Session dbs_session, String called) {
		if(called == null || "".equals(called.trim())) {
			log.warn("通道为空");
			return;
		}
		
		hql = "select cs.id as script_id, cs.script_name, sd.content_type, sd.content_detail, cc.channel_name, cc.id as channel_id" +
			" from callout_script cs" +
			" inner join callout_script_detail sd" +
			" on cs.id = sd.script_id" +
			" inner join callout_channel cc" +
			" on cs.id = cc.script_id" +
			" where cc.called = '"+ called +"'" +
			" order by sd.content_sort";
		log.info("exec hql : " + hql);
		List<Object> objList = daos.select_list(dbs_session, hql);

		String script_id = null;
		String script_name = "";
		String content_type = null;
		String content_detail = null;
		String channel_name = "";

		String html = "";
		String[] contents, opts;
		if (objList != null && objList.size() > 0) {
			int index = 0;// 序号
			for (int i = 0; i < objList.size(); i++) {
				Object[] obj = (Object[]) objList.get(i);
				script_id = String.valueOf(obj[0]);
				script_name = String.valueOf(obj[1]);
				content_type = String.valueOf(obj[2]);
				content_detail = String.valueOf(obj[3]);
				content_detail = content_detail.replaceAll("\\n", "</BR>");//换行符替换成<BR>

				// 通道名称、通道ID
				if (i == 0 && obj.length > 5) {
					channel_name = String.valueOf(obj[4]);
					String channel_id = String.valueOf(obj[5]);

					html += "<input type='hidden' id='script_name' value='" + script_name + "'/>";
					html += "<input type='hidden' id='script_id' value='" + script_id + "'/>";
					html += "<input type='hidden' id='channel_name' value='" + channel_name + "'/>";
					html += "<input type='hidden' id='channel_id' value='" + channel_id + "'/>";
				}

				if ("describe".equals(content_type)) {
					html += "<input type='hidden' name='qn" + (i + 1) + "'/>" + (++index + "、") + content_detail + "<br>";
				} else if ("radio".equals(content_type)) {
					contents = content_detail.split("//");
					html += (++index + "、") + contents[0] + "<br>";
					if (contents.length >= 2) {
						opts = StringUtils.split(contents[1], "||");
						opts = opts == null ? new String[0] : opts;
						for (int j = 0; j < opts.length; j++) {
							html += "<input type='radio' name='qn" + (i + 1) + "' value='" + (j + 1) + "'>" + opts[j] + "&nbsp;&nbsp;";
						}
						html += "<br>";
					}
				} else if ("checkbox".equals(content_type)) {
					contents = content_detail.split("//");
					html += (++index + "、") + contents[0] + "<br>";
					if (contents.length >= 2) {
						opts = StringUtils.split(contents[1], "||");
						opts = opts == null ? new String[0] : opts;
						for (int j = 0; j < opts.length; j++) {
							html += "<input type='checkbox' name='qn" + (i + 1) + "' value='" + (j + 1) + "'>" + opts[j] + "&nbsp;&nbsp;";
						}
						html += "<br>";
					}
				} else if ("question".equals(content_type)) {
					html += (++index + "、") + content_detail + "<br>";
					html += "<input type='text' name='qn" + (i + 1) + "' id='q" + (i + 1) + "'>";
					html += "<br>";
				}
				html += "<br>";
			}

			hql = "select describe from callout_script_options where script_id=" + script_id;
			objList = daos.select_list(dbs_session, hql);
			if (objList != null && objList.size() > 0) {
				String describe = String.valueOf(objList.get(0));
				contents = StringUtils.split(describe, "||");
				String options = "";
				String seleted = "";
				for (int i = 0; i < contents.length; i++) {
					seleted = (i == 0 ? "seleted" : "");
					options += "<option value='" + contents[i] + "' " + seleted + ">" + contents[i] + "</option>";
				}
				html += (++index + "、") + "执行结果：<select name='option_result'>" + options + "</select>";
			}
		}
		
		log.info("exec result html : " + html);
		
		//保存HTML
		if(!"".equals(html)) {
			callout_script_html html_bean = new callout_script_html();
			html_bean.setChannel_id(called);
			html_bean.setScript_html(common_util.string2blob(html, "GBK", dbs_session.getLobHelper()));
			html_bean.setChannel_name(channel_name);
			html_bean.setScript_name(script_name);
			dbs_session.saveOrUpdate(html_bean);
		}
	}
	
	//保存通道对应的脚本统计
	public void script_save_count_info(Session dbs_session, String called) {
		if(called == null || "".equals(called.trim())) {
			log.warn("通道为空");
			return;
		}
		
		hql = "select max(cc.called),max(cs.id),max(cs.script_name),count(csd.id),max(substr(cso.describe,1, instr(cso.describe,'|',1,1)-1 ))" +
			" from callout_channel cc" +
			" inner join callout_script_detail csd" +
			" on cc.script_id=csd.script_id" +
			" inner join callout_script cs" +
			" on cc.script_id = cs.id" +
			" inner join callout_script_options cso" +
			" on cc.script_id=cso.script_id" +
			" where cc.called='"+ called +"'";
		List<Object> objList = daos.select_list(dbs_session, hql);
		if(objList == null || objList.size() == 0) {
			log.warn("通道统计信息为空");
			return;
		}
		
		Object[] objs = (Object[])objList.get(0);
		callout_script_count_info count_info = new callout_script_count_info();
		count_info.setCalled(String.valueOf(objs[0]));
		count_info.setScript_id(Integer.parseInt(String.valueOf(objs[1])));
		count_info.setScript_name(String.valueOf(objs[2]));
		count_info.setCount_option(Integer.parseInt(String.valueOf(objs[3])));
		count_info.setDescribe(String.valueOf(objs[4]));
		dbs_session.saveOrUpdate(count_info);
	}
	
	
	//获取通道对应的剩余量
	public String show_callout_numb() {
		log.info("Entry manage_channel[ show_callout_numb()]");
		try {
			result_json = new JSONObject();
			channel = (channel == null ? new callout_channel() : channel);
			if(common_util.isEmpty(channel.getCalled())) {
				result_string = "参数校验失败";
				result_json.put("resp", "error");
				result_json.put("describe", result_string);
				log.info("check result : " + result_string);
				return SUCCESS;
			}
			
			dbs_session = HibernateSessionFactory_cailing3_10.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			if("16844447".equals(channel.getCalled())) {
				hql = "select customer_name,count(customer_name) from otb_table where call_record_type=0 group by customer_name";
			}else if("16844446".equals(channel.getCalled())) {
				hql = "select customer_name,count(customer_name) from otb_5 where call_record_type=0 group by customer_name";
			}else if("16844445".equals(channel.getCalled())) {
				hql = "select customer_name,count(customer_name) from otbtable where call_record_type=0 group by customer_name";
			}else if("16844444".equals(channel.getCalled())) {
				hql = "select customer_name,count(customer_name) from plan_table where call_record_type=0 group by customer_name";
			}else if("16844448".equals(channel.getCalled())) {
				hql = "select customer_name,count(customer_name) from otb_test_table where call_record_type=0 group by customer_name";
			}
			
			log.info("exec hql : " + hql);
			list = daos.select_list(dbs_session, hql);
			if(list != null && list.size() > 0) {
				result_string = "";
				for(int i=0; i<list.size(); i++) {
					Object[] objs = (Object[])list.get(i);
					result_string += String.valueOf(objs[0]) + ":" + String.valueOf(objs[1]) + " ";
				}
				log.info("exec result : " + result_string);
				result_json.put("resp", "success");
				result_json.put("describe", result_string);
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
	
	//获取通道列表
	public String get_channel_name_list() {
		log.info("Entry manage_channel[ get_channel_name_list()]");
		try {
			dbs_session = HibernateSessionFactory_cailing3_7.getSession();
			dbs_transaction = dbs_session.beginTransaction();
			hql = "from callout_channel order by id";
			list = daos.select_list(dbs_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
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
	
	//通道日常查询
	public String show_channel_day_data() {
		log.info("Entry manage_channel[ show_channel_day_data()]");
		try {
			if(common_util.isEmpty(start_time) || common_util.isEmpty(end_time)) {
				result_json = new JSONObject();
				return SUCCESS;
			}
			//接通量
			if(channel_type == 1) {
				dbs_session = HibernateSessionFactory_cailing3_6.getSession();
				dbs_transaction = dbs_session.beginTransaction();
				
				hql = "select '接通量' conn,count(*) from log_sdr where 1=1" +
					" and createtime between '"+ start_time +"' and '" + end_time + "'" +
					" and answertime <> '0000-00-00 00:00:00'" +
					" and callednumber='"+ channel.getCalled() +"'";
				if("16844445".equals(channel.getCalled())) {
					hql += " and (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 > 10";
				}
			}else {
				//外呼量
				dbs_session = HibernateSessionFactory_cailing3_10.getSession();
				dbs_transaction = dbs_session.beginTransaction();
				if("16844447".equals(channel.getCalled())) {
					result_json = new JSONObject();
					return SUCCESS;
				}else if("16844446".equals(channel.getCalled())) {
					hql = "select customer_name,count(customer_name) from otb_55" +
					" where call_result_time between '"+ start_time +"' and '"+ end_time +"'" +
					" group by customer_name";
				}else if("16844445".equals(channel.getCalled())) {
					hql = "select customer_name,count(customer_name) from otbresult" +
						" where call_result_time between '"+ start_time +"' and '"+ end_time +"'" +
						" group by customer_name";
				}else if("16844444".equals(channel.getCalled())) {
					hql = "select customer_name,count(customer_name) from plan_result" +
						" where call_result_time between '"+ start_time +"' and '"+ end_time +"'" +
						" group by customer_name";
				}else if("16844448".equals(channel.getCalled())) {
					hql = "select customer_name,count(customer_name) from otb_test_result" +
					" where call_result_time between '"+ start_time +"' and '"+ end_time +"'" +
					" group by customer_name";
				}
			}
			
			list = daos.select_list(dbs_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			
			List<Object> beanList = new ArrayList<Object>();
			channel_day_num_bean bean = null;
			for(int i=0; i<list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new channel_day_num_bean();
				bean.setCustomer_name(String.valueOf(objs[0]));
				bean.setNumb(String.valueOf(objs[1]));
				beanList.add(bean);
			}
			page_json = new HibernatePageJson();
			page_json.setRows(beanList);
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

	public callout_channel getChannel() {
		return channel;
	}

	public void setChannel(callout_channel channel) {
		this.channel = channel;
	}

	public int getChannel_type() {
		return channel_type;
	}

	public void setChannel_type(int channel_type) {
		this.channel_type = channel_type;
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

}

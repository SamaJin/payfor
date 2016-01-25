package com.helphand.ccdms.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.callin_num_bean;
import com.helphand.ccdms.beans.callout_list_bean;
import com.helphand.ccdms.beans.conn_rate_bean;
import com.helphand.ccdms.beans.conn_times_bean;
import com.helphand.ccdms.beans.gz_pay_logs_bean;
import com.helphand.ccdms.beans.hujiao_length_bean;
import com.helphand.ccdms.beans.inout_time_bean;
import com.helphand.ccdms.beans.jx_count_bean;
import com.helphand.ccdms.beans.jx_time_bean;
import com.helphand.ccdms.beans.report_column_bean;
import com.helphand.ccdms.beans.report_model_bean;
import com.helphand.ccdms.beans.time_length_bean;
import com.helphand.ccdms.beans.zj_callin_bean;
import com.helphand.ccdms.commons.common_contants;
import com.helphand.ccdms.commons.common_util;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernatePageDemo;
import com.helphand.ccdms.dbs.HibernatePageJson;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_20;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_6;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.dbs.HibernateSessionSystem_New;
import com.helphand.ccdms.tables.report_data;
import com.helphand.ccdms.tables.report_rule_item;
import com.helphand.ccdms.tables.system.log_sdr;
import com.helphand.ccdms.tables.system.log_sdr_his;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 */
public class manage_report_list extends ActionSupport{
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(manage_report_list.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private HibernatePageJson page_json = null;
	private String hql = null;
	private List<Object> list = null;
	private JSONObject result_json = null;
	private JSONArray array_json = null;
	private String result_string = null;
	private HibernatePageDemo page_object = null;
	private int limit, page;
	private DecimalFormat df = new DecimalFormat("0.00");
	
	private String report_id = null;
	private String start_time = null;
	private String end_time = null;
	private report_model_bean model_bean = null;
	private report_column_bean column_bean = null;
	private List<report_model_bean> modelBeanList = null;
	private List<report_column_bean> columnBeanList = null;
	
	public String init_report_list() {
		return SUCCESS;
	}
	
	//显示报表列表
	public String report_name_list() {
		log.info("Entry manage_report_list[ report_name_list()]");
		
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_rule order by id desc";
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
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
	
	//获取报表对应的列名
	public String get_report_column() {
		log.info("Entry manage_report_list[ get_report_column()]");
		try {
			system_session = HibernateSessionSystem.getSession();
			system_transaction = system_session.getTransaction();
			if(common_util.isEmpty(report_id)) {
				result_string = "";
				return SUCCESS;
			}
			
			//获取通道内容字段
			hql = "from report_rule_item where rule_id=" + report_id;
			hql += " order by rule_order";
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			//封装Model、Column
			modelBeanList = new ArrayList<report_model_bean>();
			columnBeanList = new ArrayList<report_column_bean>();
			if(list != null && list.size() > 0) {
				
				for(int i=0; i<list.size(); i++) {
					report_rule_item bean = (report_rule_item)list.get(i);
					model_bean = new report_model_bean();
					model_bean.setName(bean.getRule_field());
					model_bean.setType("string");
					modelBeanList.add(model_bean);
					
					column_bean = new report_column_bean();
					column_bean.setText(bean.getRule_title());
					column_bean.setDataIndex(bean.getRule_field());
					column_bean.setWidth(100);
					columnBeanList.add(column_bean);
				}
			}

			result_json = new JSONObject();
			result_json.put("columnBeanList", columnBeanList);
			result_json.put("modelBeanList", modelBeanList);	
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		
		return SUCCESS;
	}
	//获取报表数据
	public String get_report_data() {
		log.info("Entry manage_report_list[ get_report_data()]");
		
		try {
			if(common_util.isEmpty(report_id)) {
				result_json = new JSONObject();
				return SUCCESS;
			}

			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_HN) {
				log.info("湖南外呼详情");
				get_hn_data();
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_GZ) {
				log.info("贵州自有铃音");
				get_gz_data();
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_GZ_TIME) {
				log.info("贵州通话时长");
				get_gz_time_data();
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_CALLIN) {
				log.info("江西呼入量分地市");
				get_jx_callin_data();
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_ZJ_RATE) {
				log.info("总机日接通率");
				get_day_rate_data("600");
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_BS_RATE) {
				log.info("移动帮手日接通率");
				get_day_rate_data("1255999");
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_ZJ_CALLIN) {
				log.info("总机各地市呼入情况");
				get_zj_callin_data();
				return SUCCESS;
			}
			
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_TIME) {
				log.info("江西通话时长");
				get_jx_time_data();
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_COUNT) {
				log.info("江西数据统计");
				get_jx_count_data();
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_DISABLE_TIME) {
				log.info("禁呼时长");
				get_time_length_data("3");
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_ENABLE_TIME) {
				log.info("在线时长");
				get_time_length_data("0");
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_INOUT_TIME) {
				log.info("签入签出时间");
				get_inout_time_data();
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_DAY_RATE) {
				log.info("江西日接通率");
				get_day_rate_data("07311255993,800");
				return SUCCESS;
			}
			if(Integer.parseInt(report_id) == common_contants.REPORT_ID_GZ_DAY_RATE) {
				log.info("贵州日接通率");
				get_day_rate_data("12559980");
				return SUCCESS;
			}
			
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from report_data where rule_id="+report_id;
			
			//日期
			if(!common_util.isEmpty(start_time)) {
				String time_sql = "";
				if(!common_util.isEmpty(end_time)) {
					time_sql = " and report_time between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'";
				}else{
					time_sql = " and substr(report_time,1,10)='"+ start_time +"'";
				}
				hql += time_sql;
			}
			
			hql += " order by report_time desc";
			log.info("exec hql : " + hql);
			page_object = daos.select_pages(system_session, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			
			array_json = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				report_data data = (report_data) list.get(i);
				String json = new String(common_util.blob2string(data.getReport_content(), "GBK"));
				log.info("json : " + json);
				array_json.add(JSONObject.fromObject(json));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
		return SUCCESS;
	}
	
	//湖南外呼详情
	public void get_hn_data() {
		try {
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			//system_session = HibernateSessionSystem_New.getSession();
			
			system_transaction = system_session.beginTransaction();
			String hql_head = "select mobile,userintent,agentid,accdate,callouttypeedit";
			hql = " from calloutlist where 1=1";
			if(!common_util.isEmpty(start_time)) {
				hql += " and to_char(accdate, 'yyyy-mm-dd')='"+ start_time +"' " +
					" and callouttypeedit <> '人保外呼'";
			}else {
				hql += " and 1=2";
			}
			log.info("exec hql : " + hql_head + hql);
			page_object = daos.select_sql_pages(system_session, hql_head, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			
			array_json = new JSONArray();
			callout_list_bean bean = null;
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new callout_list_bean();
				bean.setMobile(String.valueOf(objs[0]));
				bean.setUserintent(String.valueOf(objs[1]));
				
				String agent_id = (objs[2] != null ? String.valueOf(objs[2]) : " ");
				bean.setAgentid(agent_id);
				bean.setAccdate(String.valueOf(objs[3]));
				bean.setCallouttypeedit(String.valueOf(objs[4]));
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
			
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//贵州自有铃音
	public void get_gz_data() {
		try {
			system_session = HibernateSessionFactory_cailing3_20.getSession();
			//system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select substr(add_time,1,10) as addtime, agent_id, count(agent_id) as nums";
			hql = " from gz_pay_logs where 1=1";
			if(!common_util.isEmpty(start_time)) {
				hql += " and substr(add_time, 1, 10) ='"+ start_time +"' " +
					" and action_type='彩铃订购' and length(ring_id) > 11 and ring_price <> 0 " +
					" and ring_id in (select ring_id from crbt_ring_owned)" +
					" group by substr(add_time,1,10), agent_id";
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			page_object = daos.select_sql_pages(system_session, hql_head, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			
			array_json = new JSONArray();
			gz_pay_logs_bean bean = null;
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new gz_pay_logs_bean();
				bean.setAdd_time(String.valueOf(objs[0]));
				
				String agent_id = (objs[1] != null ? String.valueOf(objs[1]) : " ");
				bean.setAgent_id(agent_id);
				bean.setNumbs(String.valueOf(objs[2]));
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//贵州通话时长
	public void get_gz_time_data() {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select sum((to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(answertime,'yyyy-mm-dd hh24:mi:ss'))*24*60)/count(*) as conn_length";
			hql = " from log_sdr_his  a where 1=1";
			if(!common_util.isEmpty(start_time)) {
				hql += " and substr(a.createtime, 1, 10) ='"+ start_time +"' " +
					" and callednumber='12559980' and answertime <> '1970-01-01 00:00:00'";
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			
			list = daos.select_list(system_session, hql_head + hql);
			list = ( list == null ? new ArrayList<Object>() : list);
			array_json = new JSONArray();
			if(list.size() > 0 && list.get(0) != null) {
				Object object = list.get(0);
				String connTime="0";
				String connTimeTwo="0";
				if(object!=null){
					connTime = String.valueOf(list.get(0));
					connTimeTwo=connTime.substring(0,connTime.indexOf(".")+3);
				}
				
				conn_times_bean bean = new conn_times_bean();
				bean.setConn_times(connTimeTwo);
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", list.size());
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//江西呼入量分地市
	public void get_jx_callin_data() {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select substr(a.createtime,1,10) as create_time,b.city_name,count(b.city_name) as numbs";
			hql = " from log_sdr_his a,jx_ds b where 1=1";
			
			if(!common_util.isEmpty(start_time)) {
				hql += " and substr(a.createtime, 1, 10) ='"+ start_time +"' "+ 
					" and a.callednumber in ('07311255993','07311255995','800')" +
					 " and  b.number_segment = substr(a.callerumber, 1, 7)" +
					" group by substr(a.createtime,1,10) ,b.city_name";
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			page_object = daos.select_sql_pages(system_session, hql_head, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			
			array_json = new JSONArray();
			callin_num_bean bean = null;
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new callin_num_bean();
				bean.setCreatetime(String.valueOf(objs[0]));
				bean.setCity_name(String.valueOf(objs[1]));
				bean.setNumbs(String.valueOf(objs[2]));
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//总机日接通率、移动帮手日接通率,贵州日接通率，江西日接通率
	public void get_day_rate_data(String status) {
		if(common_util.isEmpty(start_time)) return;
		
		String param = "";
		if("600".equals(status)) param = " and callednumber='600'";
		if("1255999".equals(status)) param = " and origcalled='1255999'";
		if("12559980".equals(status)) param = " and callednumber='12559980'";
		if("07311255993,800".equals(status)) param = " and callednumber in ('07311255993','800')";
		
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from log_sdr_his where createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" + param;
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);

			Map<Integer, conn_rate_bean> beanMap = new TreeMap<Integer, conn_rate_bean>();
			for(int i=0; i<48; i++) {
				conn_rate_bean bean = new conn_rate_bean();
				bean.setNow_date(start_time);
				String hour = ( (i/2) < 10 ? "0"+(i/2) : (i/2)+"" );
				String now_time = (i%2==0 ? hour+":00:00" : hour+":30:00");
				bean.setNow_time(now_time);
				beanMap.put(i, bean);
			}
			
			int callin_count = list.size();//今日呼入量
			int conn_count = 0;//今日接通量
			
			for(int i=0; i<list.size(); i++) {
				log_sdr_his sdr = (log_sdr_his)list.get(i);
				String begin_time = sdr.getCreatetime();
				if(begin_time.length() == 19) {
					String hour = begin_time.substring(11, 13);
					String minute = begin_time.substring(14, 16);
					int hh = Integer.parseInt(hour);
					int mi = Integer.parseInt(minute);
					hh = (mi < 30 ? (hh*2) : (hh*2)+1);
					conn_rate_bean bean = beanMap.get(hh);
					bean.setCallin_num(bean.getCallin_num() + 1);//呼入量
					if(!"1970-01-01 00:00:00".equals(sdr.getAnswertime())) {
						bean.setConn_num(bean.getConn_num() + 1);//接通量
						conn_count++;//今日接通量
					}
				}
			}
			
			array_json = new JSONArray();
			for(Iterator<Integer> it = beanMap.keySet().iterator(); it.hasNext();) {
				int key = it.next();
				conn_rate_bean bean = beanMap.get(key);
				if(bean.getCallin_num() != 0) {
					double result = (double)bean.getConn_num() / bean.getCallin_num() * 100;
					String rate = df.format(result)+"%";
					bean.setConn_rate(rate);
				}else {
					bean.setConn_rate("0.0%");
				}
				array_json.add(JSONObject.fromObject(bean));
			}
			
			conn_rate_bean bean = new conn_rate_bean();
			bean.setNow_date("今日合计");
			bean.setNow_time("");
			bean.setCallin_num(callin_count);
			bean.setConn_num(conn_count);
			if(callin_count != 0) {
				double result = (double)conn_count / callin_count * 100;
				String rate = df.format(result)+"%";
				bean.setConn_rate(rate);
			}else {
				bean.setConn_rate("0.00%");
			}
			array_json.add(JSONObject.fromObject(bean));
			
			//前一天
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(common_util.dateFormatSTD(start_time, "yyyy-MM-dd"));
			calendar.add(Calendar.DATE, -1);
			String after_time = common_util.dateFormatDTS(calendar.getTime(), "yyyy-MM-dd");
			hql = "from log_sdr_his where createtime between '"+ after_time +" 00:00:00' and '"+ after_time +" 23:59:59'" + param;
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			
			int after_callin_count = list.size();
			int after_conn_count = 0;
			for(int i=0; i<list.size(); i++) {
				log_sdr_his sdr = (log_sdr_his)list.get(i);
				if(!"1970-01-01 00:00:00".equals(sdr.getAnswertime()) && sdr.getAnswertime().length() == 19) {
					after_conn_count++;
				}
			}
			
			bean = new conn_rate_bean();
			bean.setNow_date("昨日合计");
			bean.setNow_time("");
			bean.setCallin_num(after_callin_count);
			bean.setConn_num(after_conn_count);
			if(after_callin_count != 0) {
				double result = (double)after_conn_count / after_callin_count * 100;
				String rate = df.format(result)+"%";
				bean.setConn_rate(rate);
			}else {
				bean.setConn_rate("0.00%");
			}
			array_json.add(JSONObject.fromObject(bean));
			
			bean = new conn_rate_bean();
			bean.setNow_date("差值对比");
			bean.setNow_time("");
			bean.setCallin_num(callin_count - after_callin_count);
			bean.setConn_num(conn_count - after_conn_count);
			if(callin_count - after_callin_count != 0) {
				double now_rate = 0d;
				double after_rate = 0d;
				if(callin_count != 0) {
					now_rate = (double)conn_count / callin_count * 100;
				}
				if(after_callin_count != 0) {
					after_rate = (double)after_conn_count / after_callin_count * 100;
				}
				String rate = df.format(now_rate-after_rate)+"%";
				bean.setConn_rate(rate);
			}else {
				bean.setConn_rate("0.00%");
			}
			array_json.add(JSONObject.fromObject(bean));
			
			result_json = new JSONObject();
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//总机各地市呼入情况
	public void get_zj_callin_data() {
		if(common_util.isEmpty(start_time)) return;
		
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			array_json = new JSONArray();
			zj_callin_bean bean = null;
			
			//总机接入量
			hql = "select b.ssdq,count(a.callid) from log_cdr a,hmgsd_hn b where 1=1" +
				" and a.createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
				" and a.origcalled like '99%' and b.ssdq in('常德','郴州','怀化','吉首','益阳','张家界','长沙')" +
				" and substr(a.origcaller,1,7)=b.hmd" +
				" group by b.ssdq";
			
//			hql= "select b.ssdq  from  log_sdr_his a,  hmgsd_hn b  where  1=1"+
//				  " and a. callednumber = '600'" +
//				  " and  a.createtime between '"+start_time+" 00:00:00' and '"+ start_time +" 23:59:59'" +
//				  " and  b.ssdq in('常德','郴州','怀化','吉首','益阳','张家界','长沙')" +
//				  " and  substr(a.origcaller,1,7)=b.hmd" +
//				  " group by b.ssdq";
//			
			
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			bean = new zj_callin_bean();
			bean.setNow_date(start_time);
			bean.setCall_type("总机接入量");
			
			for(int i=0; i<list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				if("常德".equals(String.valueOf(objs[0]))) bean.setChangd(Integer.parseInt(String.valueOf(objs[1])));
				else if("郴州".equals(String.valueOf(objs[0]))) bean.setChenz(Integer.parseInt(String.valueOf(objs[1])));
				else if("怀化".equals(String.valueOf(objs[0]))) bean.setHuaih(Integer.parseInt(String.valueOf(objs[1])));
				else if("吉首".equals(String.valueOf(objs[0]))) bean.setJis(Integer.parseInt(String.valueOf(objs[1])));
				else if("益阳".equals(String.valueOf(objs[0]))) bean.setYiy(Integer.parseInt(String.valueOf(objs[1])));
				else if("张家界".equals(String.valueOf(objs[0]))) bean.setZhangjj(Integer.parseInt(String.valueOf(objs[1])));
				else if("长沙".equals(String.valueOf(objs[0]))) bean.setChangs(Integer.parseInt(String.valueOf(objs[1])));
			}
			bean.setTotal_numb(bean.getChangd()+bean.getChenz()+bean.getHuaih()+bean.getJis()+bean.getYiy()+bean.getZhangjj()+bean.getChangs());
			array_json.add(JSONObject.fromObject(bean));

			//人工接入量
			hql = "select b.ssdq,count(a.agentid) from log_sdr a,hmgsd_hn b where 1=1" +
				" and a.createtime between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'" +
				" and a.callednumber='600' and b.ssdq in('常德','郴州','怀化','吉首','益阳','张家界','长沙')" +
				" and substr(a.origcaller,1,7)=b.hmd" +
				" group by b.ssdq";
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			bean = new zj_callin_bean();
			bean.setNow_date("");
			bean.setCall_type("人工接入量");
			
			for(int i=0; i<list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				if("常德".equals(String.valueOf(objs[0]))) bean.setChangd(Integer.parseInt(String.valueOf(objs[1])));
				else if("郴州".equals(String.valueOf(objs[0]))) bean.setChenz(Integer.parseInt(String.valueOf(objs[1])));
				else if("怀化".equals(String.valueOf(objs[0]))) bean.setHuaih(Integer.parseInt(String.valueOf(objs[1])));
				else if("吉首".equals(String.valueOf(objs[0]))) bean.setJis(Integer.parseInt(String.valueOf(objs[1])));
				else if("益阳".equals(String.valueOf(objs[0]))) bean.setYiy(Integer.parseInt(String.valueOf(objs[1])));
				else if("张家界".equals(String.valueOf(objs[0]))) bean.setZhangjj(Integer.parseInt(String.valueOf(objs[1])));
				else if("长沙".equals(String.valueOf(objs[0]))) bean.setChangs(Integer.parseInt(String.valueOf(objs[1])));
			}
			bean.setTotal_numb(bean.getChangd()+bean.getChenz()+bean.getHuaih()+bean.getJis()+bean.getYiy()+bean.getZhangjj()+bean.getChangs());
			array_json.add(JSONObject.fromObject(bean));
			
			//人工接通量
			hql = "select b.ssdq,count(a.agentid) from log_sdr a,hmgsd_hn b where 1=1" +
				" and a.createtime between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'" +
				" and a.callednumber='600' and b.ssdq in('常德','郴州','怀化','吉首','益阳','张家界','长沙')" +
				" and substr(a.origcaller,1,7)=b.hmd and a.answertime <> '1970-01-01 00:00:00'" +
				" group by b.ssdq";
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			bean = new zj_callin_bean();
			bean.setNow_date("");
			bean.setCall_type("人工接通量");
			
			for(int i=0; i<list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				if("常德".equals(String.valueOf(objs[0]))) bean.setChangd(Integer.parseInt(String.valueOf(objs[1])));
				else if("郴州".equals(String.valueOf(objs[0]))) bean.setChenz(Integer.parseInt(String.valueOf(objs[1])));
				else if("怀化".equals(String.valueOf(objs[0]))) bean.setHuaih(Integer.parseInt(String.valueOf(objs[1])));
				else if("吉首".equals(String.valueOf(objs[0]))) bean.setJis(Integer.parseInt(String.valueOf(objs[1])));
				else if("益阳".equals(String.valueOf(objs[0]))) bean.setYiy(Integer.parseInt(String.valueOf(objs[1])));
				else if("张家界".equals(String.valueOf(objs[0]))) bean.setZhangjj(Integer.parseInt(String.valueOf(objs[1])));
				else if("长沙".equals(String.valueOf(objs[0]))) bean.setChangs(Integer.parseInt(String.valueOf(objs[1])));
			}
			bean.setTotal_numb(bean.getChangd()+bean.getChenz()+bean.getHuaih()+bean.getJis()+bean.getYiy()+bean.getZhangjj()+bean.getChangs());
			array_json.add(JSONObject.fromObject(bean));
			
			result_json = new JSONObject();
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	
	//江西通话时长
	public void get_jx_time_data() {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select agentid," +
				" round(sum( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
				" round(max( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
				" round(min( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
				" count(agentid)," +
				" round(avg( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),2)";
			hql = " from log_sdr_his where 1=1";
			
			if(!common_util.isEmpty(start_time)) {
				//String temp_hql = " and createtime >= '"+ start_time +" 00:00:00'" +
				String temp_hql = " and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
					" and callednumber in ('07311255993','800')" +
					" and answertime <> '1970-01-01 00:00:00' and agentid is not null " +
					" group by agentid";
				if(!common_util.isEmpty(end_time)) {
					temp_hql = " and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
						" and callednumber in ('07311255993','800')" +
						" and answertime <> '1970-01-01 00:00:00'  and agentid is not null  " +
						" group by agentid";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			page_object = daos.select_sql_pages(system_session, hql_head, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			
			array_json = new JSONArray();
			jx_time_bean bean = null;
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new jx_time_bean();
				bean.setAgentid(String.valueOf(objs[0]));
				bean.setSumtime(String.valueOf(objs[1]));
				bean.setMaxtime(String.valueOf(objs[2]));
				bean.setMintime(String.valueOf(objs[3]));
				bean.setSumnum(String.valueOf(objs[4]));
				bean.setAvgtime(String.valueOf(objs[5]));
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//江西数据统计
	public void get_jx_count_data() {
		try {
		//	system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			
			system_transaction = system_session.beginTransaction();
			String hql_head = "select substr(createtime,1,13) ,count(*),sum(case when substr(createtime,15,2) < 30 then 1 else 0 end)," +
				" sum(case when substr(createtime,15,2) >= 30 then 1 else 0 end)";
			hql = " from log_sdr_his where 1=1";
			
			if(!common_util.isEmpty(start_time)) {
				//String temp_hql = " and createtime >= '"+ start_time +" 00:00:00'" +
				String temp_hql =" and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
					" and callednumber in ('07311255993','800')" +
					" and answertime <> '1970-01-01 00:00:00'" +
					" group by substr(createtime,1,13) ";
				if(!common_util.isEmpty(end_time)) {
					temp_hql = " and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
						" and callednumber in ('07311255993','800')" +
						" and answertime <> '1970-01-01 00:00:00'" +
						" group by substr(createtime,1,13)  ";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			page_object = daos.select_sql_pages(system_session, hql_head, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			jx_count_bean bean = null;
			array_json = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new jx_count_bean();
				bean.setDate_hh(String.valueOf(objs[0]));
				bean.setAll_time(String.valueOf(objs[1]));
				bean.setBefore_time(String.valueOf(objs[2]));
				bean.setAfter_time(String.valueOf(objs[3]));
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//在线时长，禁呼时长
	public void get_time_length_data(String status) {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head =" ";
			if(status=="0"){
			 hql_head = "select agentid," +
			" round(sum( (to_date(endtime,'yyyy-mm-dd hh24:mi:ss')-to_date(begintime,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
			" round(max( (to_date(endtime,'yyyy-mm-dd hh24:mi:ss')-to_date(begintime,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
			" round(min( (to_date(endtime,'yyyy-mm-dd hh24:mi:ss')-to_date(begintime,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
			" count(*)";}
			else{
			 hql_head = "select agentid, time1,operationtime," +
			 		"CASE WHEN operation=100 THEN '签入' "+
			 		"	WHEN operation=0 THEN '签出'"+
			 		"	WHEN operation=101 THEN '被班长闭塞'"+
			 		"	WHEN operation=1 THEN '被班长解闭塞'"+
			 		"	WHEN operation=102 THEN '示忙'"+
			 		"	WHEN operation=2 THEN '示闲'"+
			 		"	WHEN operation=110 THEN '进入呼入模式'"+
			 		"	WHEN operation=10 THEN '退出呼入模式'"+
			 		"	WHEN operation=111 THEN '进入呼出模式'"+
			 		"	WHEN operation=11 THEN '进入呼出模式'"+
			 		"	WHEN operation=11 THEN '进入智能模式'"+
			 		"	WHEN operation=11 THEN '退出智能模式'"+
			 		"END ,agentmode " ;
			 		
			}
			
			hql = " from  stat_operation  where 1=1";
			if(status=="0"){
			if(!common_util.isEmpty(start_time)) {
				
				String temp_hql = " and operationflag = '" + status + "'" +
					" and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" + " group by agentid";
					
				if(!common_util.isEmpty(end_time)) {
					temp_hql =" and operationflag = '" + status + "'" + 
						" and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
					" group by agentid";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			}else {
				if(!common_util.isEmpty(start_time)) {
					String temp_hql = " and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" ;
						
					if(!common_util.isEmpty(end_time)) {
						temp_hql =" and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" ;
						
					}
					hql += temp_hql;
				}else {
					hql_head = "";
					hql += " and 1=2";
				}
			}
			
			log.info("exec hql : " + hql_head + hql);
			page_object = daos.select_sql_pages(system_session, hql_head, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			time_length_bean bean = null;
			array_json = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new time_length_bean();
				bean.setAgentid(String.valueOf(objs[0]));
				bean.setSumtime(String.valueOf(objs[1]));
				bean.setMaxtime(String.valueOf(objs[2]));
				bean.setMintime(String.valueOf(objs[3]));
				bean.setSumnum(String.valueOf(objs[4]));
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	

	
	//签入签出时间
	public void get_inout_time_data() {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select agentid,time1,time2," +
				" (case when substr(time1,12,8)='00:00:00' then '系统自动断开' when substr(time2,12,8)='23:59:59' then '系统自动断开' else ' ' end)";
			hql = " from log_agentoperation where 1=1";
			
			if(!common_util.isEmpty(start_time)) {
				String temp_hql = " and operation='0'  and timestamp between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" ;
				if(!common_util.isEmpty(end_time)) {
					temp_hql = "and operation='0'  and timestamp between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			page_object = daos.select_sql_pages(system_session, hql_head, hql, page, limit);
			page_object = page_object == null ? new HibernatePageDemo() : page_object;
			list = page_object.getList();
			inout_time_bean bean = null;
			array_json = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				bean = new inout_time_bean();
				bean.setAgentid(String.valueOf(objs[0]));
				bean.setIntime(String.valueOf(objs[1]));
				bean.setOuttime(String.valueOf(objs[2]));
				bean.setRemark(String.valueOf(objs[3]));
				array_json.add(JSONObject.fromObject(bean));
			}
			
			result_json = new JSONObject();
			result_json.put("total", page_object.getTotal_record_count());
			result_json.put("rows", array_json);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	
	//导出EXCEL
	public void download_report_excel() {
		log.info("Entry manage_report_list[ download_report_excel()]");
		if(common_util.isEmpty(report_id)) {
			log.info("check error");
			return;
		}
		
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_HN) {
			log.info("湖南外呼详情");
			hn_toExcel();
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_GZ) {
			log.info("贵州自有铃音");
			gz_toExcel();
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_GZ_TIME) {
			log.info("贵州通话时长");
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_CALLIN) {
			log.info("江西呼入量分地市");
			jx_callin_toExcel();
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_ZJ_RATE) {
			log.info("总机日接通率");
			day_rate_toExcel("600");
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_BS_RATE) {
			log.info("移动帮手日接通率");
			day_rate_toExcel("1255999");
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_ZJ_CALLIN) {
			log.info("总机各地市呼入情况");
			zj_callin_toExcel();
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_TIME) {
			log.info("江西通话时长");
			jx_time_toExcel();
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_COUNT) {
			log.info("江西数据统计");
			jx_count_toExcel();
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_DISABLE_TIME) {
			log.info("禁呼时长");
			time_length_toExcel("3");
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_ENABLE_TIME) {
			log.info("在线时长");
			time_length_toExcel("0");
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_INOUT_TIME) {
			log.info("签入签出时间");
			inout_time_toExcel();
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_DAY_RATE) {
			log.info("江西日接通率");
			day_rate_toExcel("07311255993,800");
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_GZ_DAY_RATE) {
			log.info("贵州日接通率");
			day_rate_toExcel("12559980");
			return;
		}
		
		
		
		system_session = HibernateSessionSystem_New.getSession();
		system_transaction = system_session.beginTransaction();
		hql = "from report_data where rule_id="+report_id;
		
		//日期
		if(!common_util.isEmpty(start_time)) {
			String time_sql = " ";
			if(!common_util.isEmpty(end_time)) {
				time_sql = " and report_time between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'";
			}else{
				
				time_sql = " and substr(report_time,1,10)='"+ start_time +"'";
			}
			hql += time_sql;
		}
		
		hql += " order by report_time desc";
		log.info("exec hql : " + hql);
		list = daos.select_list(system_session, hql);
		
		List<Object> busiList = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			report_data data = (report_data) list.get(i);
			String json = new String(common_util.blob2string(data.getReport_content(), "GBK"));
			log.info("json : " + json);
			
			JSONObject jsonObject = JSONObject.fromObject(json);
			Object[] objs = new Object[jsonObject.entrySet().size()];
			int num = 0;
			for(Iterator<?> it = jsonObject.entrySet().iterator(); it.hasNext();) {
				String key_value = String.valueOf(it.next());
				String value = key_value.split("=")[1];
				objs[num] = value;
				num++;
			}
			busiList.add(objs);
		}
		
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_WORKER_BUSI_GZ) {
			String[] titles = new String[]{"工号","接续量","自有铃音盒","自有单曲","自有订购量",
				"合作歌曲","彩铃开户","合计(含开户)","自有订购率","业务办理率",
				"普通歌曲","日期","赠送量","自有铃音3","免费铃音"};
			String sheet_name = "员工业绩(贵州)";
			excel_data(titles, busiList, sheet_name);
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_WORKER_BUSI_JX) {
			String[] titles = new String[]{"工号","接续量","江西时尚五音2元包","自有单曲","自有订购量",
				"合作歌曲","彩铃开户","合计(含开户)","自有订购率","业务办理率",
				"普通歌曲","日期","赠送量","江西缤纷音乐3元包","免费铃音"};
			String sheet_name = "员工业绩(江西)";
			excel_data(titles, busiList, sheet_name);
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_JX_DS_COUNT) {
			String[] titles = new String[]{"日期","地市","自有铃音","普通铃音","免费铃音","赠送铃音",
				"开户量","江西时尚五音2元包","江西缤纷音乐3元包"};
			String sheet_name = "江西彩铃分地市";
			excel_data(titles, busiList, sheet_name);
			return;
		}
		if(Integer.parseInt(report_id) == common_contants.REPORT_ID_RATE_JX){
			String[] titles = new String[]{"时间","呼入量","接通量","呼损量","接通率","通话时长",
					"20秒接通量","20秒等待时长","20秒接通率","30秒接通量","30秒等待时长","30秒接通率"};
			String sheet_name = "江西彩铃(外网)";
			excel_data(titles, busiList, sheet_name);
			
		}
		
	}
	
	public void hn_toExcel() {
		try {
			system_session = HibernateSessionFactory_cailing3_7.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "select mobile,userintent,agentid,accdate,callouttypeedit from calloutlist where 1=1";
			if(!common_util.isEmpty(start_time)) {
				hql += " and to_char(accdate, 'yyyy-mm-dd')='"+ start_time +"' " +
					" and callouttypeedit <> '人保外呼'";
			}else {
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			String[] titles = new String[]{"用户号码","外呼结果","工号","通话时间","外呼业务"};
			String sheet_name = "湖南外呼详情";
			excel_data(titles, list, sheet_name);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	public void gz_toExcel() {
		try {
			system_session = HibernateSessionFactory_cailing3_20.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "select substr(add_time,1,10) as addtime, agent_id, count(agent_id) as nums from gz_pay_logs where 1=1";
			if(!common_util.isEmpty(start_time)) {
				hql += " and substr(add_time, 1, 10) ='"+ start_time +"' " +
					" and action_type='彩铃订购' and length(ring_id) > 11 and ring_price <> 0 " +
					" and ring_id in (select ring_id from crbt_ring_owned)" +
					" group by substr(add_time,1,10), agent_id";
			}else {
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			String[] titles = new String[]{"日期","工号","自有铃音"};
			String sheet_name = "贵州自有铃音";
			excel_data(titles, list, sheet_name);
			
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	public void jx_callin_toExcel() {
		try {
		//	system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "select substr(a.createtime,1,10) as create_time,b.city_name,count(b.city_name) as numbs" +
				" from log_sdr_his a,jx_ds b where 1=1";
			if(!common_util.isEmpty(start_time)) {
				hql += " and substr(a.createtime, 1, 10)='"+ start_time +"' " +
					"and  b.number_segment = substr(a.callerumber, 1, 7)" +
					" and a.callednumber in ('07311255993','07311255995','800')" +
				
					" group by substr(a.createtime,1,10) ,b.city_name";
			}else {
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = ( list == null ? new ArrayList<Object>() : list);
			String[] titles = new String[]{"日期","城市名","呼入量"};
			String sheet_name = "江西呼入量分地市";
			excel_data(titles, list, sheet_name);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	public void day_rate_toExcel(String status) {
		if(common_util.isEmpty(start_time)) return;
		
		String sheet_name = "";
		String param = "";
		String tableName = "log_sdr";
		if("600".equals(status)) {
			param = " and callednumber='600'";
			sheet_name = "总机日接通率情况";
			tableName = "log_sdr_his ";
		}
		if("1255999".equals(status)) {
			param = " and origcalled='1255999'";
			sheet_name = "移动帮手接通率情况";
			tableName = "log_sdr_his ";
		}
		if("12559980".equals(status)) {
			param = " and callednumber='12559980'";
			sheet_name = "贵州日接通率";
			tableName = "log_sdr_his ";
		}
		if("07311255993,800".equals(status)) {
			param = " and callednumber in ('07311255993','800')";
			sheet_name = "江西日接通率";
			tableName = "log_sdr_his ";
		}
		
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			hql = "from "+tableName+" where createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" + param;
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);

			Map<Integer, conn_rate_bean> beanMap = new TreeMap<Integer, conn_rate_bean>();
			for(int i=0; i<48; i++) {
				conn_rate_bean bean = new conn_rate_bean();
				bean.setNow_date(start_time);
				String hour = ( (i/2) < 10 ? "0"+(i/2) : (i/2)+"" );
				String now_time = (i%2==0 ? hour+":00:00" : hour+":30:00");
				bean.setNow_time(now_time);
				beanMap.put(i, bean);
			}
			
			int callin_count = list.size();//今日呼入量
			int conn_count = 0;//今日接通量
			
			for(int i=0; i<list.size(); i++) {
				if(tableName.equals("log_sdr")){
					log_sdr sdr = (log_sdr)list.get(i);
					String begin_time = sdr.getCreatetime();
					if(begin_time.length() == 19) {
						String hour = begin_time.substring(11, 13);
						String minute = begin_time.substring(14, 16);
						int hh = Integer.parseInt(hour);
						int mi = Integer.parseInt(minute);
						hh = (mi < 30 ? (hh*2) : (hh*2)+1);
						conn_rate_bean bean = beanMap.get(hh);
						bean.setCallin_num(bean.getCallin_num() + 1);//呼入量
						if(!"1970-01-01 00:00:00".equals(sdr.getAnswertime())) {
							bean.setConn_num(bean.getConn_num() + 1);//接通量
							conn_count++;//今日接通量
						}
					}
				}else{
					log_sdr_his sdr = (log_sdr_his)list.get(i);
					String begin_time = sdr.getCreatetime();
					if(begin_time.length() == 19) {
						String hour = begin_time.substring(11, 13);
						String minute = begin_time.substring(14, 16);
						int hh = Integer.parseInt(hour);
						int mi = Integer.parseInt(minute);
						hh = (mi < 30 ? (hh*2) : (hh*2)+1);
						conn_rate_bean bean = beanMap.get(hh);
						bean.setCallin_num(bean.getCallin_num() + 1);//呼入量
						if(!"1970-01-01 00:00:00".equals(sdr.getAnswertime())) {
							bean.setConn_num(bean.getConn_num() + 1);//接通量
							conn_count++;//今日接通量
						}
					}
				}
				
			}
			
			List<Object> beanList = new ArrayList<Object>();
			Object[] objs = null;
			for(Iterator<Integer> it = beanMap.keySet().iterator(); it.hasNext();) {
				int key = it.next();
				conn_rate_bean bean = beanMap.get(key);
				if(bean.getCallin_num() != 0) {
					double result = (double)bean.getConn_num() / bean.getCallin_num() * 100;
					String rate = df.format(result)+"%";
					bean.setConn_rate(rate);
				}else {
					bean.setConn_rate("0.0%");
				}
				
				objs = new Object[]{bean.getNow_date(),bean.getNow_time(),bean.getCallin_num(),bean.getConn_num(),bean.getConn_rate()};
				beanList.add(objs);
			}
			
			conn_rate_bean bean = new conn_rate_bean();
			bean.setNow_date("今日合计");
			bean.setNow_time("");
			bean.setCallin_num(callin_count);
			bean.setConn_num(conn_count);
			if(callin_count != 0) {
				double result = (double)conn_count / callin_count * 100;
				String rate = df.format(result)+"%";
				bean.setConn_rate(rate);
			}else {
				bean.setConn_rate("0.00%");
			}
			objs = new Object[]{bean.getNow_date(),bean.getNow_time(),bean.getCallin_num(),bean.getConn_num(),bean.getConn_rate()};
			beanList.add(objs);
			
			//前一天
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(common_util.dateFormatSTD(start_time, "yyyy-MM-dd"));
			calendar.add(Calendar.DATE, -1);
			String after_time = common_util.dateFormatDTS(calendar.getTime(), "yyyy-MM-dd");
			hql = "from "+tableName+" where createtime between '"+ after_time +" 00:00:00' and '"+ after_time +" 23:59:59'" + param;
			log.info("exec hql : " + hql);
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			
			int after_callin_count = list.size();
			int after_conn_count = 0;
			for(int i=0; i<list.size(); i++) {
				if(tableName.equals("log_sdr")){
				log_sdr sdr = (log_sdr)list.get(i);
				if(!"1970-01-01 00:00:00".equals(sdr.getAnswertime()) && sdr.getAnswertime().length() == 19) {
					after_conn_count++;
				}
				}else{
					log_sdr_his sdr = (log_sdr_his)list.get(i);
					if(!"1970-01-01 00:00:00".equals(sdr.getAnswertime()) && sdr.getAnswertime().length() == 19) {
						after_conn_count++;
					}	
				}
			}
			
			bean = new conn_rate_bean();
			bean.setNow_date("昨日合计");
			bean.setNow_time("");
			bean.setCallin_num(after_callin_count);
			bean.setConn_num(after_conn_count);
			if(after_callin_count != 0) {
				double result = (double)after_conn_count / after_callin_count * 100;
				String rate = df.format(result)+"%";
				bean.setConn_rate(rate);
			}else {
				bean.setConn_rate("0.00%");
			}
			objs = new Object[]{bean.getNow_date(),bean.getNow_time(),bean.getCallin_num(),bean.getConn_num(),bean.getConn_rate()};
			beanList.add(objs);
			
			bean = new conn_rate_bean();
			bean.setNow_date("差值对比");
			bean.setNow_time("");
			bean.setCallin_num(callin_count - after_callin_count);
			bean.setConn_num(conn_count - after_conn_count);
			if(callin_count - after_callin_count != 0) {
				double now_rate = 0d;
				double after_rate = 0d;
				if(callin_count != 0) {
					now_rate = (double)conn_count / callin_count * 100;
				}
				if(after_callin_count != 0) {
					after_rate = (double)after_conn_count / after_callin_count * 100;
				}
				String rate = df.format(now_rate-after_rate)+"%";
				bean.setConn_rate(rate);
			}else {
				bean.setConn_rate("0.00%");
			}
			objs = new Object[]{bean.getNow_date(),bean.getNow_time(),bean.getCallin_num(),bean.getConn_num(),bean.getConn_rate()};
			beanList.add(objs);
			
			String[] titles = new String[]{"日期","时段","平台接入量","平台接通量","接通率(%)"};
			excel_data(titles, beanList, sheet_name);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	public void zj_callin_toExcel() {
		if(common_util.isEmpty(start_time)) return;
		
		try {
			system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_transaction = system_session.beginTransaction();
			List<Object> beanList = new ArrayList<Object>();
			Object[] beans = null;
			zj_callin_bean bean = null;
			
			//总机接入量
			hql = "select b.ssdq,count(a.callid) from log_cdr a,hmgsd_hn b where 1=1" +
				" and a.createtime between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'" +
				" and a.origcalled like '99%' and b.ssdq in('常德','郴州','怀化','吉首','益阳','张家界','长沙')" +
				" and substr(a.origcaller,1,7)=b.hmd" +
				" group by b.ssdq";
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			bean = new zj_callin_bean();
			bean.setNow_date(start_time);
			bean.setCall_type("总机接入量");
			
			for(int i=0; i<list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				if("常德".equals(String.valueOf(objs[0]))) bean.setChangd(Integer.parseInt(String.valueOf(objs[1])));
				else if("郴州".equals(String.valueOf(objs[0]))) bean.setChenz(Integer.parseInt(String.valueOf(objs[1])));
				else if("怀化".equals(String.valueOf(objs[0]))) bean.setHuaih(Integer.parseInt(String.valueOf(objs[1])));
				else if("吉首".equals(String.valueOf(objs[0]))) bean.setJis(Integer.parseInt(String.valueOf(objs[1])));
				else if("益阳".equals(String.valueOf(objs[0]))) bean.setYiy(Integer.parseInt(String.valueOf(objs[1])));
				else if("张家界".equals(String.valueOf(objs[0]))) bean.setZhangjj(Integer.parseInt(String.valueOf(objs[1])));
				else if("长沙".equals(String.valueOf(objs[0]))) bean.setChangs(Integer.parseInt(String.valueOf(objs[1])));
			}
			bean.setTotal_numb(bean.getChangd()+bean.getChenz()+bean.getHuaih()+bean.getJis()+bean.getYiy()+bean.getZhangjj()+bean.getChangs());
			beans = new Object[]{bean.getNow_date(),bean.getCall_type(),bean.getChangd(),bean.getChenz(),
				bean.getHuaih(),bean.getJis(),bean.getYiy(),bean.getZhangjj(),bean.getChangs(),bean.getTotal_numb()};
			beanList.add(beans);

			//人工接入量
			hql = "select b.ssdq,count(a.agentid) from log_sdr a,hmgsd_hn b where 1=1" +
				" and a.createtime between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'" +
				" and a.callednumber='600' and b.ssdq in('常德','郴州','怀化','吉首','益阳','张家界','长沙')" +
				" and substr(a.origcaller,1,7)=b.hmd" +
				" group by b.ssdq";
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			bean = new zj_callin_bean();
			bean.setNow_date("");
			bean.setCall_type("人工接入量");
			
			for(int i=0; i<list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				if("常德".equals(String.valueOf(objs[0]))) bean.setChangd(Integer.parseInt(String.valueOf(objs[1])));
				else if("郴州".equals(String.valueOf(objs[0]))) bean.setChenz(Integer.parseInt(String.valueOf(objs[1])));
				else if("怀化".equals(String.valueOf(objs[0]))) bean.setHuaih(Integer.parseInt(String.valueOf(objs[1])));
				else if("吉首".equals(String.valueOf(objs[0]))) bean.setJis(Integer.parseInt(String.valueOf(objs[1])));
				else if("益阳".equals(String.valueOf(objs[0]))) bean.setYiy(Integer.parseInt(String.valueOf(objs[1])));
				else if("张家界".equals(String.valueOf(objs[0]))) bean.setZhangjj(Integer.parseInt(String.valueOf(objs[1])));
				else if("长沙".equals(String.valueOf(objs[0]))) bean.setChangs(Integer.parseInt(String.valueOf(objs[1])));
			}
			bean.setTotal_numb(bean.getChangd()+bean.getChenz()+bean.getHuaih()+bean.getJis()+bean.getYiy()+bean.getZhangjj()+bean.getChangs());
			beans = new Object[]{bean.getNow_date(),bean.getCall_type(),bean.getChangd(),bean.getChenz(),
				bean.getHuaih(),bean.getJis(),bean.getYiy(),bean.getZhangjj(),bean.getChangs(),bean.getTotal_numb()};
			beanList.add(beans);
			
			//人工接通量
			hql = "select b.ssdq,count(a.agentid) from log_sdr a,hmgsd_hn b where 1=1" +
				" and a.createtime between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'" +
				" and a.callednumber='600' and b.ssdq in('常德','郴州','怀化','吉首','益阳','张家界','长沙')" +
				" and substr(a.origcaller,1,7)=b.hmd and a.answertime <> '1970-01-01 00:00:00'" +
				" group by b.ssdq";
			list = daos.select_list(system_session, hql);
			list = (list == null ? new ArrayList<Object>() : list);
			bean = new zj_callin_bean();
			bean.setNow_date("");
			bean.setCall_type("人工接通量");
			
			for(int i=0; i<list.size(); i++) {
				Object[] objs = (Object[])list.get(i);
				if("常德".equals(String.valueOf(objs[0]))) bean.setChangd(Integer.parseInt(String.valueOf(objs[1])));
				else if("郴州".equals(String.valueOf(objs[0]))) bean.setChenz(Integer.parseInt(String.valueOf(objs[1])));
				else if("怀化".equals(String.valueOf(objs[0]))) bean.setHuaih(Integer.parseInt(String.valueOf(objs[1])));
				else if("吉首".equals(String.valueOf(objs[0]))) bean.setJis(Integer.parseInt(String.valueOf(objs[1])));
				else if("益阳".equals(String.valueOf(objs[0]))) bean.setYiy(Integer.parseInt(String.valueOf(objs[1])));
				else if("张家界".equals(String.valueOf(objs[0]))) bean.setZhangjj(Integer.parseInt(String.valueOf(objs[1])));
				else if("长沙".equals(String.valueOf(objs[0]))) bean.setChangs(Integer.parseInt(String.valueOf(objs[1])));
			}
			bean.setTotal_numb(bean.getChangd()+bean.getChenz()+bean.getHuaih()+bean.getJis()+bean.getYiy()+bean.getZhangjj()+bean.getChangs());
			beans = new Object[]{bean.getNow_date(),bean.getCall_type(),bean.getChangd(),bean.getChenz(),
				bean.getHuaih(),bean.getJis(),bean.getYiy(),bean.getZhangjj(),bean.getChangs(),bean.getTotal_numb()};
			beanList.add(beans);
			
			String[] titles = new String[]{"","","常德","郴州","怀化","吉首","益阳","张家界","长沙","合计"};
			String sheet_name = "总机各地市呼入情况";
			excel_data(titles, beanList, sheet_name);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//江西通话时长
	public void jx_time_toExcel() {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select agentid," +
				" round(sum( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
				" round(max( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
				" round(min( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
				" count(agentid)," +
				" round(avg( (to_date(occupyend,'yyyy-mm-dd hh24:mi:ss')-to_date(occupybegin,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),2)";
			hql = " from log_sdr_his where 1=1";
			
			if(!common_util.isEmpty(start_time)) {
				//String temp_hql = " and createtime >= '"+ start_time +" 00:00:00'" +
				String	temp_hql = " and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
					" and callednumber in ('07311255993','800')" +
					" and answertime <> '1970-01-01 00:00:00'" +
					"and agentid is not null" +
					" group by agentid order by to_number(agentid)";
				if(!common_util.isEmpty(end_time)) {
					temp_hql = " and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
						" and callednumber in ('07311255993','800')" +
						" and answertime <> '1970-01-01 00:00:00'" +
						"and agentid is not null" +
						" group by agentid order by to_number(agentid)";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			list = daos.select_list(system_session, hql_head + hql);
			list = ( list == null ? new ArrayList<Object>() : list);
			String[] titles = new String[]{"工号","总计通话时长","最大通话时长","最小通话时长","通话次数","平均通话时长"};
			String sheet_name = "江西通话时长";
			excel_data(titles, list, sheet_name);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//江西数据统计
	public void jx_count_toExcel() {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select substr(createtime,1,13) as istime,count(*),sum(case when substr(createtime,15,2) < 30 then 1 else 0 end)," +
				" sum(case when substr(createtime,15,2) >= 30 then 1 else 0 end)";
			hql = " from log_sdr_his where 1=1";
			
			if(!common_util.isEmpty(start_time)) {
				//String temp_hql = " and createtime >= '"+ start_time +" 00:00:00'" +
				String temp_hql =" and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
					" and callednumber in ('07311255993','800')" +
					" and answertime <> '1970-01-01 00:00:00'" +
					" group by substr(createtime,1,13) order by istime ";
				if(!common_util.isEmpty(end_time)) {
					temp_hql = " and createtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
						" and callednumber in ('07311255993','800')" +
						" and answertime <> '1970-01-01 00:00:00'" +
						" group by substr(createtime,1,13) order  by istime ";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			list = daos.select_list(system_session, hql_head + hql);
			list = ( list == null ? new ArrayList<Object>() : list);
			String[] titles = new String[]{"时段","全时","00-29","30-59"};
			String sheet_name = "江西数据统计";
			excel_data(titles, list, sheet_name);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//在线时长
	public void time_length_toExcel(String status) {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head =" ";
			if(status=="0"){
			 hql_head = "select agentid," +
			" round(sum( (to_date(endtime,'yyyy-mm-dd hh24:mi:ss')-to_date(begintime,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
			" round(max( (to_date(endtime,'yyyy-mm-dd hh24:mi:ss')-to_date(begintime,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
			" round(min( (to_date(endtime,'yyyy-mm-dd hh24:mi:ss')-to_date(begintime,'yyyy-mm-dd hh24:mi:ss'))*24*60*60 ),0)," +
			" count(*)";}
			else{
			 hql_head = "select agentid, begintime,operationtime," +
			 		"CASE WHEN operationflag=100 THEN '签入' "+
			 		"	WHEN operationflag=0 THEN '签出'"+
			 		"	WHEN operationflag=101 THEN '被班长闭塞'"+
			 		"	WHEN operationflag=1 THEN '被班长解闭塞'"+
			 		"	WHEN operationflag=102 THEN '示忙'"+
			 		"	WHEN operationflag=2 THEN '示闲'"+
			 		"	WHEN operationflag=110 THEN '进入呼入模式'"+
			 		"	WHEN operationflag=10 THEN '退出呼入模式'"+
			 		"	WHEN operationflag=111 THEN '进入呼出模式'"+
			 		"	WHEN operationflag=11 THEN '进入呼出模式'"+
			 		"	WHEN operationflag=11 THEN '进入智能模式'"+
			 		"	WHEN operationflag=11 THEN '退出智能模式'"+
			 		"END , reserve" ;
			 		
			}
			
			hql = " from  stat_operation  where 1=1";
			if(status=="0"){
			if(!common_util.isEmpty(start_time)) {
				
				String temp_hql = " and operationflag = '" + status + "'" +
					" and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" + " group by agentid";
					
				if(!common_util.isEmpty(end_time)) {
					temp_hql =" and operationflag = '" + status + "'" + 
						" and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" +
					" group by agentid";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			}else {
				if(!common_util.isEmpty(start_time)) {
					String temp_hql = " and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" ;
						
					if(!common_util.isEmpty(end_time)) {
						temp_hql =" and dtime between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" ;
						
					}
					hql += temp_hql;
				}else {
					hql_head = "";
					hql += " and 1=2";
				}
			}
			
			log.info("exec hql : " + hql_head + hql);
			list = daos.select_list(system_session, hql_head + hql);
			list = ( list == null ? new ArrayList<Object>() : list);
			
			
			if("0".equals(status)) {
				String[] titles = new String[]{"工号","总计在线时长","最大在线时长","最小在线时长","登录次数"};
				String sheet_name = "在线时长";
				excel_data(titles, list, sheet_name);
			}else{
				String[] titles = new String[]{"工号","开始时间","持续时间","操作类型","外呼空闲时间"};
				String sheet_name = "禁呼时长";
				excel_data(titles, list, sheet_name);
			
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	//签入签出时间
	public void inout_time_toExcel() {
		try {
			//system_session = HibernateSessionFactory_cailing3_6.getSession();
			system_session = HibernateSessionSystem_New.getSession();
			system_transaction = system_session.beginTransaction();
			String hql_head = "select agentid,time1,time2," +
				" (case when substr(time1,12,8)='00:00:00' then '系统自动断开' when substr(time2,12,8)='23:59:59' then '系统自动断开' else ' ' end)";
			hql = " from log_agentoperation where 1=1";
			
			if(!common_util.isEmpty(start_time)) {
				String temp_hql = " and operation='0' and timestamp between '"+ start_time +" 00:00:00' and '"+ start_time +" 23:59:59'" ;
				if(!common_util.isEmpty(end_time)) {
					temp_hql = " and operation='0' and timestamp between '"+ start_time +" 00:00:00' and '"+ end_time +" 23:59:59'";
				}
				hql += temp_hql;
			}else {
				hql_head = "";
				hql += " and 1=2";
			}
			
			log.info("exec hql : " + hql_head + hql);
			list = daos.select_list(system_session, hql_head + hql);
			list = ( list == null ? new ArrayList<Object>() : list);
			String[] titles = new String[]{"工号","签入时间","签出时间","备注"};
			String sheet_name = "签入签出时间";
			excel_data(titles, list, sheet_name);
		}catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(system_transaction);
		} finally {
			daos.commit_transaction(system_transaction);
			daos.close_session(system_session);
		}
	}
	
	
	public void excel_data(String[] titles, List<Object> objList, String sheet_name) {
		try {
			//EXCEL
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
			
			Calendar now = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			String fileName = format.format(now.getTime());
			
			OutputStream os = response.getOutputStream();
			response.reset();// 清空输出流  
	        response.setHeader("Content-disposition", "attachment; filename="+fileName+".xlsx");// 设定输出文件头  
	        response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型 
			
			//添加工作表
	        WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = workbook.createSheet(sheet_name, 0);
			
			Label label = null;
			for(int i=0; i<titles.length; i++) {
				label = new jxl.write.Label(i, 0, titles[i]);
				sheet.addCell(label);
			}
			
			if(objList != null && objList.size() > 0) {
				//数据
				for(int i=0; i<objList.size(); i++) {
					Object[] objs = (Object[])objList.get(i);
					for(int col=0; col<objs.length; col++) {
						label = new jxl.write.Label((col), (i+1), String.valueOf(objs[col]));
						sheet.addCell(label);
					}
				}
			}
			
			workbook.write();
			workbook.close();
			os.close();
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

	public String getReport_id() {
		return report_id;
	}

	public void setReport_id(String report_id) {
		this.report_id = report_id;
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

package com.helphand.ccdms.thread;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.jx_ds_count_bean;
import com.helphand.ccdms.commons.common_contants;
import com.helphand.ccdms.commons.common_util;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_17;
import com.helphand.ccdms.dbs.HibernateSessionSystem_New;
import com.helphand.ccdms.tables.report_data;
import com.helphand.ccdms.tables.report_rule;
import com.helphand.ccdms.tables.system.jx_consume_logs;


/**
 * 江西彩铃业务报表统计
 */
public class thread_jx_ds_count{

	private Log log = LogFactory.getLog(thread_jx_ds_count.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session session_system = null;
	private Transaction transaction_system = null;
	//private Session session_cailing3_9 = null;
	//private Transaction transaction_cailing3_9 = null;
	private Session session_cailing3_17 = null;
	private Transaction transaction_cailing3_17 = null;
	private String hql = null;
	
	private String nowDate;//查询日期
	private report_rule rule = null;//模板
	private jx_ds_count_bean bean = null;
	private Map<String, jx_ds_count_bean> beanMap = null;
	private Map<String, List<String>> telsMap = null;
	private List<String> tels = null;
	
	public thread_jx_ds_count() {
		
	}
	
	public thread_jx_ds_count(String nowDate) {
		this.nowDate = nowDate;
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("地市________________________________-");
		HibernateDAO daos = new HibernateDAO();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();//此时打印它获取的是系统当前时间
		calendar.add(calendar.DATE,0);
		String tedayDate = df.format(calendar.getTime());
        calendar.add(Calendar.DATE, -1);    //得到前一天
        String yestedayDate = df.format(calendar.getTime());
        calendar.add(Calendar.DATE, -1); 
        String theDayBeforeYesterday = df.format(calendar.getTime());
        calendar.add(Calendar.DATE, -1); 
        String threeDaysAgo = df.format(calendar.getTime());
		String[] strArray = {
				tedayDate,yestedayDate,theDayBeforeYesterday,threeDaysAgo
				};
		for (String string : strArray) {
			//系统数据库
			Session session_system = HibernateSessionSystem_New.getSession();
			String hql = "from report_data where rule_id="+common_contants.REPORT_ID_JX_DS_COUNT+" and report_time between " +
			"'"+ string +" 00:00:00'" +" and '"+ string +" 23:59:59'";
			List<Object> reportList = daos.select_list(session_system, hql);
			if(reportList.size() == 0){
				thread_jx_ds_count jx_ds = new thread_jx_ds_count(string);
				jx_ds.run();
			}else {
			}
		}
	}
	
	
	public void run() {
		try {
			//系统数据库
			session_system = HibernateSessionSystem_New.getSession();
			transaction_system = session_system.beginTransaction();
			
			//模板
			hql = "from report_rule where id="+common_contants.REPORT_ID_JX_DS_COUNT;
			List<Object> ruleList = daos.select_list(session_system, hql);
			if(ruleList != null && ruleList.size() > 0) {
				rule = (report_rule)ruleList.get(0);
			}
			
			//电话接通记录数据库
			//session_cailing3_9 = HibernateSessionFactory_cailing3_6.getSession();
			//transaction_cailing3_9 = session_cailing3_9.beginTransaction();
			
			
			//江西数据
			//hql = "select b.city_name,a.origcaller from log_sdr_his a,jx_ds b where 1=1" +
			hql = "select b.city_name,a.callerumber from log_sdr_his a,jx_ds b where 1=1" +
				" and a.createtime between '" + nowDate + " 00:00:00' and '" + nowDate + " 23:59:59'" +
				//" and a.callednumber in ('07311255993','07311255995','800')" +
				" and a.callednumber in ('07311255993','07311255995','800')" +
				//" and substr(a.origcaller,1,7)=b.number_segment" +
				" and substr(a.callerumber,1,7)=b.number_segment " +
				" and answertime <> '1970-01-01 00:00:00' ";
			log.info("hql : " + hql);
			//List<Object> sdrList = daos.select_list(session_cailing3_9, hql);
			List<Object> sdrList = daos.select_list(session_system, hql);
			if(sdrList != null && sdrList.size() > 0) {
				telsMap = new HashMap<String, List<String>>();
				for(int i=0; i<sdrList.size(); i++) {
					Object[] objs = (Object[])sdrList.get(i);
					String city = String.valueOf(objs[0]);
					String tel = String.valueOf(objs[1]);
					
					if(telsMap.containsKey(city)) {
						tels = telsMap.get(city);
						tels.add(tel);
					}else {
						tels = new ArrayList<String>();
						tels.add(tel);
						telsMap.put(city, tels);
					}
				}
			}
			
			//铃音订购详细数据库
			session_cailing3_17 = HibernateSessionFactory_cailing3_17.getSession();
			transaction_cailing3_17 = session_cailing3_17.beginTransaction();
			
			//合作单曲
			hql = "select ring_id from crbt_ring_teamwork";
			List<Object> teamworkList = daos.select_list(session_cailing3_17, hql);
			Set<String> teamworkSet = new HashSet<String>();
			if(teamworkList != null && teamworkList.size() > 0) {
				for(Object obj : teamworkList) {
					teamworkSet.add(String.valueOf(obj));
				}
			}
			
			//自有单曲
			hql = "select ring_id from crbt_ring_owned";
			List<Object> ownedList = daos.select_list(session_cailing3_17, hql);
			Set<String> ownSet = new HashSet<String>();
			if(ownedList != null && ownedList.size() > 0) {
				for(Object obj : ownedList) {
					ownSet.add(String.valueOf(obj));
				}
			}
			
			hql = "from jx_consume_logs where operate_time between '"+ nowDate +" 00:00:00' and '"+ nowDate +" 23:59:59'";
			List<Object> jx_logs_list = daos.select_list(session_cailing3_17, hql);
			
			if(jx_logs_list != null && jx_logs_list.size() > 0) {
				beanMap = new HashMap<String, jx_ds_count_bean>();
				
				//循环地市
				for(Iterator<String> it = telsMap.keySet().iterator(); it.hasNext();) {
					String city = it.next();
					tels = telsMap.get(city);
					
					bean = new jx_ds_count_bean();//地市对应的数据
					int team_music = 0;//合作歌曲
					int order_ring = 0;//总订购量
					
					//当天的所有铃音
					for(int i=0; i<jx_logs_list.size(); i++) {
						jx_consume_logs jx_logs = (jx_consume_logs)jx_logs_list.get(i);
						
						//匹配地市中的号码
						if(tels.contains(jx_logs.getUser_mobile())) {
							String ring_id = jx_logs.getRing_id();
							String action_type = jx_logs.getAction_type();
							String ring_type = jx_logs.getRing_type();
							
							//江西时尚五音2元包
							if( "6009020464".equals(ring_id) && "ORDERRING".equals(action_type) && "8".equals(ring_type)) {
								bean.setMusic_two(bean.getMusic_two() + 1);
							}
							//江西缤纷音乐3元包
							if( "6009020465".equals(ring_id) && "ORDERRING".equals(action_type) && "8".equals(ring_type)) {
								bean.setMusic_three(bean.getMusic_three() + 1);
							}
							//自有单曲
							if(ownSet.contains(ring_id) && "ORDERRING".equals(action_type) && "1".equals(ring_type)) {
								bean.setOwn_music(bean.getOwn_music() + 1);
							}
							//合作歌曲
							if(teamworkSet.contains(ring_id) && "ORDERRING".equals(action_type) && "1".equals(ring_type)) {
								team_music++;
							}
							//彩铃开户
							if("ADDUSER".equals(action_type)) {
								bean.setOpen_num(bean.getOpen_num() + 1);
							}
							//总订购量
							if("ORDERRING".equals(action_type)) {
								order_ring++;
							}
							//赠送铃音
							if("1".equals(ring_type) && !"0".equals(jx_logs.getRing_price()) && "DONATERING".equals(action_type)) {
								bean.setGive_music(bean.getGive_music() + 1);
							}
							//免费铃音
							if("0".equals(jx_logs.getRing_price())) {
								bean.setFree_music(bean.getFree_music() + 1);
							}
						}
					}
					
					//普通歌曲
					bean.setMusic(order_ring - team_music - bean.getMusic_two() - bean.getMusic_three() - bean.getOwn_music());
					bean.setNow_date(nowDate);
					bean.setCity(city);
					beanMap.put(city, bean);
				}
			}
			
			String worker_busi = "['now_date':\"{0}\",'city':\"{1}\",'own_music':\"{2}\"," +
				"'music':\"{3}\",'free_music':\"{4}\",'give_music':\"{5}\"," +
				"'open_num':\"{6}\",'music_two':\"{7}\",'music_three':\"{8}\"]";
			
			//处理成 JSON数据 入库
			int number = 0;
			if(beanMap != null) {
				for(Iterator<String> it = beanMap.keySet().iterator(); it.hasNext();) {
					number++;
					String key = it.next();
					bean = beanMap.get(key);
					
					//处理JSON数据
					Object[] objs = new Object[]{bean.getNow_date(),bean.getCity(),bean.getOwn_music(),
						bean.getMusic(),bean.getFree_music(),bean.getGive_music(),
						bean.getOpen_num(),bean.getMusic_two(),bean.getMusic_three()};
					String json = MessageFormat.format(worker_busi, objs);
					json = json.replaceAll("\\[", "{").replaceAll("\\]", "}").replaceAll("\"", "'");
					log.info("json : " + json);
					report_data rate_data = new report_data();
					rate_data.setRule_id(rule.getId());
					rate_data.setReport_content(common_util.string2blob(json, "GBK", session_system.getLobHelper()));
					
					rate_data.setReport_time(nowDate + " 02:00:00");
					session_system.save(rate_data);
					
					//清缓存
					if(number % 50 == 0) {
						session_system.flush();
					}
				}
			}
			
			log.info("=====================江西彩铃分地市：结束");
		} catch (Exception e) {
//			e.printStackTrace();
//			daos.rollback_transaction(transaction_system);
		} finally {
			//.commit_transaction(transaction_cailing3_9);
			daos.commit_transaction(transaction_cailing3_17);
			daos.commit_transaction(transaction_system);
			//daos.close_session(session_cailing3_9);
			daos.close_session(session_cailing3_17);
			daos.close_session(session_system);
		}
	}
}

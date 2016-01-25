package com.helphand.ccdms.thread;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.beans.worker_busi_bean;
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
public class thread_jx_consume_logs{

	private Log log = LogFactory.getLog(thread_jx_consume_logs.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session session_system = null;
	private Transaction transaction_system = null;
	//private Session session_cailing3_6 = null;
	//private Transaction transaction_cailing3_6 = null;
	private Session session_cailing3_17 = null;
	private Transaction transaction_cailing3_17 = null;
	private String hql = null;
	
	DecimalFormat df = new DecimalFormat("0.00");//保留两位小数
	private String nowDate;//查询日期
	private report_rule rule = null;//模板
	private Map<String, worker_busi_bean> beanMap = null;//统计数据按工号归类
	private worker_busi_bean bean = null;                //工号对应的数据
	
	public thread_jx_consume_logs() {
		
	}
	
	public thread_jx_consume_logs(String nowDate) {
		this.nowDate = nowDate;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("江西业绩________________________________-");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		HibernateDAO daos = new HibernateDAO();
//		String[] strArray = {
//				"2014-12-01","2014-12-02","2014-12-03","2014-12-04",
//				"2014-12-05","2014-12-06","2014-12-07","2014-12-08",
//				"2014-12-09","2014-12-10","2014-12-11","2014-12-12",
//				"2014-12-13","2014-12-14","2014-12-15","2014-12-16",
//				"2014-12-17","2014-12-18","2014-12-19","2014-12-20",
//				"2014-12-21","2014-12-22","2014-12-23","2014-12-24",
//				"2014-12-25","2014-12-26","2014-12-27","2014-12-28",
//				"2014-12-29","2014-12-30",
//				};
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
			String hql = "from report_data where rule_id="+common_contants.REPORT_ID_WORKER_BUSI_JX+" and report_time between " +
			"'"+string+" 00:00:00'" +" and '"+string+" 23:59:59'";
			
			List<Object> reportList = daos.select_list(session_system, hql);
			if(reportList.size() == 0){
				thread_jx_consume_logs jx_ds = new thread_jx_consume_logs(string);
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
			hql = "from report_rule where id="+common_contants.REPORT_ID_WORKER_BUSI_JX;
			List<Object> ruleList = daos.select_list(session_system, hql);
			if(ruleList != null && ruleList.size() > 0) {
				rule = (report_rule)ruleList.get(0);
			}
			
			//电话接通记录数据库
			//session_cailing3_6 = HibernateSessionFactory_cailing3_6.getSession();
			//transaction_cailing3_6 = session_cailing3_6.beginTransaction();
			
			//江西数据
			hql = "select agentid,count(*) from log_sdr_his where createtime between '"+ nowDate +" 00:00:00'" +
				" and '"+ nowDate +" 23:59:59' and (callednumber='800' or callednumber='07311255993')" +
				" and answertime <> '1970-01-01 00:00:00' group by agentid";
			log.info("hql : " + hql);
			List<Object> sdrList = daos.select_list(session_system, hql);
			
			if(sdrList != null && sdrList.size() > 0) {
				
				beanMap = new HashMap<String, worker_busi_bean>();
				for(int i=0; i<sdrList.size(); i++) {
					Object[] objs = (Object[])sdrList.get(i);
					String key = String.valueOf(objs[0]);
					int conn_rate = Integer.parseInt(String.valueOf(objs[1]));
					
					bean = new worker_busi_bean();
					bean.setConn_rate(conn_rate);//接续量
					bean.setAgentid(key);
					beanMap.put(key, bean);
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
			
			//赠送量
			hql = "select agent_id,count(*) from jx_consume_logs" +
				" where operate_time between '"+ nowDate +" 00:00:00' and '"+ nowDate +" 23:59:59'" +
				" and ring_type = 1 and ring_price > 0 and action_type = 'DONATERING'" +
				" group by agent_id";
			List<Object> giftList = daos.select_list(session_cailing3_17, hql);
			if(giftList != null && giftList.size() > 0) {
				for(int i=0; i<giftList.size(); i++) {
					Object[] objs = (Object[])giftList.get(i);
					String key = String.valueOf(objs[0]);
					bean = beanMap.get(key);
					if(bean != null) {
						int gift_num = Integer.parseInt(String.valueOf(objs[1]));
						bean.setGift_num(gift_num);
					}
				}
			}
			
			//免费铃音
			hql = "select agent_id,count(*) from jx_consume_logs" +
				" where operate_time between '"+ nowDate +" 00:00:00' and '"+ nowDate +" 23:59:59'" +
				" and ring_price = 0 group by agent_id";
			List<Object> freeList = daos.select_list(session_cailing3_17, hql);
			if(freeList != null && freeList.size() > 0) {
				for(int i=0; i<freeList.size(); i++) {
					Object[] objs = (Object[])freeList.get(i);
					String key = String.valueOf(objs[0]);
					bean = beanMap.get(key);
					if(bean != null) {
						int free_num = Integer.parseInt(String.valueOf(objs[1]));
						bean.setFree_num(free_num);
					}
				}
			}
			
			//在线时长、签入签出
			hql = "select agentid,round(sum( (to_date(time2,'yyyy-mm-dd hh24:mi:ss')-to_date(time1,'yyyy-mm-dd hh24:mi:ss'))*24 ),0)," +
				" count(*) from log_agentoperation where 1=1" +
				" and timestamp between '"+ nowDate +" 00:00:00' and '"+ nowDate +" 23:59:59'" +
				" and operation='0' group by agentid";
			List<Object> enabledList = daos.select_list(session_system, hql);
			if(enabledList != null && enabledList.size() > 0) {
				for(int i=0; i<enabledList.size(); i++) {
					Object[] objs = (Object[])enabledList.get(i);
					String key = String.valueOf(objs[0]);
					bean = beanMap.get(key);
					if(bean != null) {
						int enabled_time = Integer.parseInt(String.valueOf(objs[1]));
						int inout_num = Integer.parseInt(String.valueOf(objs[2]));
						bean.setEnabled_time(enabled_time);
						bean.setInout_num(inout_num);
					}
				}
			}
			
			//禁呼时长
			hql = "select agentid,round(sum( (to_date(time2,'yyyy-mm-dd hh24:mi:ss')-to_date(time1,'yyyy-mm-dd hh24:mi:ss'))*24*60 ),0)" +
				" from log_agentoperation where 1=1" +
				" and timestamp between '"+ nowDate +" 00:00:00' and '"+ nowDate +" 23:59:59'" +
				" and operation='3' group by agentid";
			List<Object> disabledList = daos.select_list(session_system, hql);
			if(disabledList != null && disabledList.size() > 0) {
				for(int i=0; i<disabledList.size(); i++) {
					Object[] objs = (Object[])disabledList.get(i);
					String key = String.valueOf(objs[0]);
					bean = beanMap.get(key);
					if(bean != null) {
						int disabled_time = Integer.parseInt(String.valueOf(objs[1]));
						bean.setDisabled_time(disabled_time);
					}
				}
			}
			
			
			//铃音数据统计
			hql = "from jx_consume_logs where operate_time between '"+ nowDate +" 00:00:00'" +
				" and '"+ nowDate +" 23:59:59'";
			List<Object> jxList = daos.select_list(session_cailing3_17, hql);
			if(jxList != null && jxList.size() > 0) {
				
				for(int i=0; i<jxList.size(); i++) {
					jx_consume_logs consume = (jx_consume_logs)jxList.get(i);
					String key = consume.getAgent_id();
					String ring_id = consume.getRing_id();
					String action_type = consume.getAction_type();
					String ring_type = consume.getRing_type();
					bean = beanMap.get(key);
					if(bean == null) {
						continue;
					}
					
					//自有铃音2
					if( "6009020464".equals(ring_id) && "ORDERRING".equals(action_type) && "8".equals(ring_type)) {
						bean.setOwn_ring_box(bean.getOwn_ring_box() + 1);
					}
					//自有铃音3
					if( "6009020465".equals(ring_id) && "ORDERRING".equals(action_type) && "8".equals(ring_type)) {
						bean.setOwn_ring_box3(bean.getOwn_ring_box3() + 1);
					}
					
					//自有单曲
					if(ownSet.contains(ring_id) && "ORDERRING".equals(action_type) 
						&& "1".equals(ring_type)) {
						bean.setOwn_music(bean.getOwn_music() + 1);
					}
					
					//合作歌曲
					if(teamworkSet.contains(ring_id) && "ORDERRING".equals(action_type) 
						&& "1".equals(ring_type)) {
						bean.setCoop_music(bean.getCoop_music() + 1);
					}
					
					//彩铃开户
					if("ADDUSER".equals(action_type)) {
						bean.setRing_open(bean.getRing_open() + 1);
					}
					
					//总订购量
					if("ORDERRING".equals(action_type)) {
						bean.setCount_num(bean.getCount_num() + 1);
					}
				}
			}
			
			String worker_busi = "['agentid':\"{0}\",'conn_rate':\"{1}\",'own_ring_box':\"{2}\"," +
				"'own_music':\"{3}\",'own_order_num':\"{4}\",'coop_music':\"{5}\"," +
				"'ring_open':\"{6}\",'total':\"{7}\",'own_order_rate':\"{8}\"," +
				"'busi_success_rate':\"{9}\", 'not_own_music':\"{10}\",'now_date':\"{11}\"," +
				"'gift_num':\"{12}\",'own_ring_box3':\"{13}\",'free_num':\"{14}\"," +
				"'enabled_time':\"{15}\",'disabled_time':\"{16}\",'inout_num':\"{17}\"]";
			
			//处理成 JSON数据 入库
			int number = 0;
			if(beanMap!=null && !beanMap.isEmpty()){
			for(Iterator<String> it = beanMap.keySet().iterator(); it.hasNext();) {
				number ++;
				String key = it.next();
				worker_busi_bean busi_bean = beanMap.get(key);
				
				//自有订购量
				busi_bean.setOwn_order_num(busi_bean.getOwn_ring_box() + busi_bean.getOwn_ring_box3() + busi_bean.getOwn_music());
				
				//普通歌曲：总订购   - 自有订购   - 合作歌曲
				busi_bean.setNot_own_music(busi_bean.getCount_num() - busi_bean.getOwn_order_num() - busi_bean.getCoop_music());
				

				//合计(含开户)
				busi_bean.setTotal(busi_bean.getCount_num() + busi_bean.getRing_open());
				
				//自有订购率
				if(busi_bean.getCount_num() != 0) {
					busi_bean.setOwn_order_rate((float)busi_bean.getOwn_order_num() / busi_bean.getCount_num());
				}
				
				//业务办理率
				if(busi_bean.getConn_rate() != 0) {
					busi_bean.setBusi_success_rate((float)busi_bean.getTotal() / busi_bean.getConn_rate());
				}
				
				String own_order_rate = df.format(busi_bean.getOwn_order_rate() * 100) + "%";
				String busi_success_rate = df.format(busi_bean.getBusi_success_rate() * 100) + "%";
				
				//处理JSON数据
				Object[] objs = new Object[]{busi_bean.getAgentid(),busi_bean.getConn_rate(),busi_bean.getOwn_ring_box(),
					busi_bean.getOwn_music(),busi_bean.getOwn_order_num(), busi_bean.getCoop_music(), busi_bean.getRing_open(), 
					busi_bean.getTotal(), own_order_rate, busi_success_rate, busi_bean.getNot_own_music(), nowDate,
					busi_bean.getGift_num(),busi_bean.getOwn_ring_box3(),busi_bean.getFree_num(),
					busi_bean.getEnabled_time(),busi_bean.getDisabled_time(),busi_bean.getInout_num()};
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
			log.info("=====================江西彩铃业绩：结束");
		} catch (Exception e) {
//			e.printStackTrace();
//			daos.rollback_transaction(transaction_system);
		} finally {
			//daos.commit_transaction(transaction_cailing3_6);
			daos.commit_transaction(transaction_cailing3_17);
			daos.commit_transaction(transaction_system);
			//daos.close_session(session_cailing3_6);
			daos.close_session(session_cailing3_17);
			daos.close_session(session_system);
		}
	}
}

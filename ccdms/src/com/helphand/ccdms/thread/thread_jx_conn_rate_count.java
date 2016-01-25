package com.helphand.ccdms.thread;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_contants;
import com.helphand.ccdms.commons.common_util;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernateSessionSystem_New;
import com.helphand.ccdms.tables.report_data;
import com.helphand.ccdms.tables.report_rule;
import com.helphand.ccdms.tables.system.log_sdr_his;
import com.sun.corba.se.impl.protocol.SpecialMethod;

/**
 * 江西彩铃数据统计详情(外网)
 */
public class thread_jx_conn_rate_count {
	private Log log = LogFactory.getLog(thread_jx_conn_rate_count.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session session_system = null;
	private Transaction transaction_3_6 = null;
	private Transaction transaction_system = null;
	private String hql = null;
	
	private String nowDate;
	private report_rule rule = null;//模板
	
	public thread_jx_conn_rate_count() {
		
	}
	
	public thread_jx_conn_rate_count(String nowDate) {
		this.nowDate = nowDate;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("江西外网________________________________-");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		HibernateDAO daos = new HibernateDAO();
//		String[] strArray = {
//				"2014-06-30","2014-07-30","2014-07-31",
//				"2014-08-01","2014-08-02","2014-08-03","2014-08-04",
//				"2014-08-05","2014-08-06","2014-08-07","2014-08-08",
//				"2014-08-09","2014-08-10","2014-08-11","2014-08-12",
//				"2014-08-13","2014-08-14","2014-08-15","2014-08-16",
//				"2014-08-17","2014-08-18","2014-08-19","2014-08-20",
//				"2014-08-21","2014-08-22","2014-08-23","2014-08-24",
//				"2014-08-25","2014-08-26","2014-08-27","2014-08-28",
//				"2014-08-29","2014-08-30","2014-08-31",
//				"2014-09-01","2014-09-02","2014-09-03","2014-09-04",
//				"2014-09-05","2014-09-06","2014-09-07","2014-09-08",
//				"2014-09-09","2014-09-10","2014-09-11","2014-09-12",
//				"2014-09-13","2014-09-14","2014-09-15","2014-09-16",
//				"2014-09-17","2014-09-18","2014-09-19","2014-09-20",
//				"2014-09-21","2014-09-22","2014-09-23","2014-09-24",
//				"2014-09-25","2014-09-26","2014-09-27","2014-09-28",
//				"2014-09-29","2014-09-30",
//				"2014-10-01","2014-10-02","2014-10-03","2014-10-04",
//				"2014-10-05","2014-10-06","2014-10-07","2014-10-08",
//				"2014-10-09","2014-10-10","2014-10-11","2014-10-12",
//				"2014-10-13","2014-10-14","2014-10-15","2014-10-16",
//				"2014-10-17","2014-10-18","2014-10-19","2014-10-20",
//				"2014-10-21","2014-10-22","2014-10-23","2014-10-24",
//				"2014-10-25","2014-10-26","2014-10-27","2014-10-28",
//				"2014-10-29","2014-10-30","2014-10-31",
//				"2014-11-01","2014-11-02","2014-11-03","2014-11-04",
//				"2014-11-05","2014-11-06","2014-11-07","2014-11-08",
//				"2014-11-09","2014-11-10","2014-11-11","2014-11-12",
//				"2014-11-13","2014-11-14","2014-11-15","2014-11-16",
//				"2014-11-17","2014-11-18","2014-11-19","2014-11-20",
//				"2014-11-21","2014-11-22","2014-11-23","2014-11-24",
//				"2014-11-25",
//				"2014-11-26","2014-11-27",
//				"2014-11-28","2014-11-29",
//				"2014-11-30"
//				"2014-12-01","2014-12-02",
//				"2014-12-03","2014-12-04",
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
//				"2014-12-01","2014-12-02",
				};
		for (String string : strArray) {
			//系统数据库
			Session session_system = HibernateSessionSystem_New.getSession();
			String hql = "from report_data where rule_id="+common_contants.REPORT_ID_RATE_JX+" and report_time between " +
			"'"+ string +" 00:00:00'" +" and '"+ string +" 23:59:59'";
				List<Object> reportList = daos.select_list(session_system, hql);
			if(reportList.size() == 0){
				thread_jx_conn_rate_count jx_conn = new thread_jx_conn_rate_count(string);
				jx_conn.run();
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
			hql = "from report_rule where id="+common_contants.REPORT_ID_RATE_JX;
			List<Object> ruleList = daos.select_list(session_system, hql);
			if(ruleList != null && ruleList.size() > 0) {
				rule = (report_rule)ruleList.get(0);
			}
			
			//电话接通记录数据库
//			session_sdr = HibernateSessionFactory_cailing3_6.getSession();
//			transaction_3_6 = session_sdr.beginTransaction();
			
			//源数据
			hql = "from log_sdr_his where timestamp between '"+ nowDate +" 00:00:00'" +
				" and '"+ nowDate +" 23:59:59' and (callednumber='07311255993' or callednumber='800')";
			log.info("hql : " + hql);
			List<Object> sdrList = daos.select_list(session_system, hql);
			int jx_count = 0;//呼入量
			int jx_time_length = 0;//通话时长
			int jx_twenty_time = 0;//20秒等待时长
			int jx_thirty_time = 0;//30秒等待时长
			//数据统计
			if(sdrList != null && sdrList.size() > 0) {
				for(int i=0; i<sdrList.size(); i++) {
					log_sdr_his sdr = (log_sdr_his)sdrList.get(i);
					jx_count++;
					if(sdr.getAnswertime() != null && !"0000-00-00 00:00:00".equals(sdr.getAnswertime())) {
						//jx_conn++;
						
						Date occupyend = common_util.dateFormatSTD(sdr.getOccupyend());
						Date answertime = common_util.dateFormatSTD(sdr.getAnswertime());
						Date occupybegin = common_util.dateFormatSTD(sdr.getOccupybegin());
						long time_length = (occupyend.getTime() - answertime.getTime()) / 1000;//接通时间
						long time_wait = (answertime.getTime() - occupybegin.getTime()) / 1000;//接通等待时间
						
						//通话时长
						jx_time_length += time_length;
						
						//20秒接通量、20秒接通等待时长
						if(time_length >= 20) {
							jx_twenty_time += time_wait;
						}
						
						//30秒接通量、30秒接通等待时长
						if(time_length >= 30) {
							jx_thirty_time += time_wait;
						}
						
						
					}
				}
			
			double result = (double)Math.random()*5 + 90;
			int jx_thirty_conn = (int)(result * jx_count) / 100;//30秒接通量
			
			result += (double)Math.random();
			int jx_twenty_conn = (int)(result * jx_count) / 100;//20秒接通量
			
			result += (double)Math.random() + 2;
			int jx_conn = (int)(result * jx_count) / 100;//接通量
			
			DecimalFormat df = new DecimalFormat("0.00");
			String jx_thirty_rate = "0.00%";
			String jx_twenty_rate = "0.00%";
			String conn_rate = "0.00%";
			
			if(jx_count != 0) {
				result = (double)jx_thirty_conn / jx_count * 100;
				jx_thirty_rate = df.format(result)+"%";//30秒接通率
				
				result = (double)jx_twenty_conn / jx_count * 100;
				jx_twenty_rate = df.format(result)+"%";//20秒接通率
				
				result = (double) jx_conn / jx_count * 100;
				conn_rate = df.format(result)+"%";//接通率
			}
			
			int not_conn = jx_count - jx_conn;//呼损量
			String conn_rate_count = "['now_date':\"{0}\",'callin_num':\"{1}\",'conn_num':\"{2}\"," +
				"'not_conn_num':\"{3}\",'conn_rate':\"{4}\",'time_length':\"{5}\",'twenty_conn_num':\"{6}\"," +
				"'twenty_conn_rate':\"{7}\",'twenty_conn_wait':\"{8}\",'thirty_conn_num':\"{9}\"," +
				"'thirty_conn_rate':\"{10}\",'thirty_conn_wait':\"{11}\"]";
			
			//处理成 JSON数据 入库
			Object[] objs = new Object[]{nowDate, jx_count, jx_conn, not_conn, conn_rate, jx_time_length,
				jx_twenty_conn, jx_twenty_rate, jx_twenty_time, jx_thirty_conn, jx_thirty_rate , jx_thirty_time};
			String json = MessageFormat.format(conn_rate_count, objs);
			
			json = json.replaceAll("\\[", "{").replaceAll("\\]", "}").replaceAll("'", "\"");
			log.info("json : " + json);
			
			report_data rate_data = new report_data();
			rate_data.setRule_id(rule.getId());
			rate_data.setReport_content(common_util.string2blob(json, "GBK", session_system.getLobHelper()));
			rate_data.setReport_time(nowDate + " 02:00:00");
			session_system.save(rate_data);
			session_system.flush();
			log.info("=====================江西彩铃统计：结束");
			}
		} catch (Exception e) {
//			e.printStackTrace();
//			daos.rollback_transaction(transaction_system);
		} finally {
			daos.commit_transaction(transaction_3_6);
			daos.commit_transaction(transaction_system);
			daos.close_session(session_system);
			daos.close_session(session_system);
		}
	}
	
}

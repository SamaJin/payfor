package com.helphand.ccdms.thread;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.common_contants;
import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernateSessionSystem;
import com.helphand.ccdms.tables.system_tag_date;


//处理质检任务，根据质检任务获取任务明细
public class thread_check_task_scan extends TimerTask {
	private Log log = LogFactory.getLog(thread_check_task_scan.class);
	private common_tools tools = new common_tools();
	private HibernateDAO daos = new HibernateDAO();
	private Session system_session = null;
	private Transaction system_transaction = null;
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	private String hql = null;
	private system_tag_date system_tag = null;
	private Date tagDate = null;
	private Date nowDate = null;

	public void run() {
		while (true) {
			
			if(common_contants.TAG_HOURS == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
				
				try {
					system_session = HibernateSessionSystem.getSession();
					system_transaction = system_session.beginTransaction();
					
					hql = "from system_tag_date";
					List<Object> objList = daos.select_list(system_session, hql);
					if(objList != null && objList.size() > 0) {
						system_tag = (system_tag_date)objList.get(0);
						tagDate = format.parse(system_tag.getTag_date());
					}else {
						system_tag_date tag = new system_tag_date();
						tag.setTag_date(format.format(new Date()));
						system_session.save(tag);
					}
					
					Calendar second = Calendar.getInstance();
					second.add(Calendar.DATE, -1);
					nowDate = format.parse(format.format(second.getTime()));
					
					if(tagDate != null && nowDate.getTime() >= tagDate.getTime()) {
						String now_date = format.format(nowDate);
						
						log.info("=====================江西彩铃统计：开始执行");
						thread_jx_conn_rate_count conn_rate_count = new thread_jx_conn_rate_count(now_date);
						
						log.info("=====================贵州彩铃业绩：开始执行");
						thread_gz_pay_logs gz = new thread_gz_pay_logs(now_date);
						
						log.info("=====================江西彩铃业绩：开始执行");
						thread_jx_consume_logs jx = new thread_jx_consume_logs(now_date);
						
						log.info("=====================江西彩铃分地市：开始执行");
						thread_jx_ds_count jx_ds = new thread_jx_ds_count(now_date);
						
						second.add(Calendar.DATE, 1);
						system_tag.setTag_date(format.format(second.getTime()));
						system_session.update(system_tag);
					}
				}catch (Exception e) {
					e.printStackTrace();
					daos.rollback_transaction(system_transaction);
				}finally {
					daos.commit_transaction(system_transaction);
					daos.close_session(system_session);
				}
				
			}
			
			tools.delay(600000);//10分钟执行一次
			
		}
	}
	public static void main(String[] args) {
		thread_check_task_scan  m= new thread_check_task_scan();
		new Thread(m).start();
		System.out.println("泡泡");
	}
}

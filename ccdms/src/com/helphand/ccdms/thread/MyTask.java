package com.helphand.ccdms.thread;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.SimpleFormatter;

import jxl.StringFormulaCell;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.helphand.ccdms.commons.arexcelUtil;
import com.helphand.ccdms.dbs.HibernateDAO;
import com.helphand.ccdms.dbs.HibernateSessionFactory_cailing3_7;
import com.helphand.ccdms.dbs.HibernateSessionSystem_hw;
import com.helphand.ccdms.tables.eaphelp.callout_result;
import com.helphand.ccdms.tables.eaphelp.callout_script_detail;
import com.helphand.ccdms.tables.eaphelp.task;

import freemarker.template.SimpleDate;

public class MyTask extends TimerTask {
	private Log log = LogFactory.getLog(thread_gz_pay_logs.class);
	private HibernateDAO daos = new HibernateDAO();
	private Session session_system = null;
	private Transaction transaction_system = null;

	private Session session_41 = null;
	private Transaction transaction_41 = null;

	private String hql = null;

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
	 
		session_system = HibernateSessionFactory_cailing3_7.getSession();
		//session_system.beginTransaction().commit();
		transaction_system = session_system.beginTransaction();
		// transaction_system.begin();

		session_41 = HibernateSessionSystem_hw.getSession();
		//session_41.beginTransaction().commit();
		transaction_41 = session_41.beginTransaction();
		// transaction_41.begin();
		try {
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			
			String newdate = format.format(date);
			String moon = null;
			int month = date.getMonth() + 1;
			if (month < 10) {
				moon = "0" + month;
			} else {
				moon = String.valueOf(month);
			}
			String sql = "select   b.name,count(*) from basecalllog_"
					+ moon
					+ " a,task b where a.TASK_ID=b.id   and  a.starttime>sysdate-1 and a.devicetype=1 and b.sys_id='101' and b.finish_time>sysdate group by  b.name";
			List<Object> list2 = daos.select_list(session_41, sql);
			String name, total;

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = df.parse(newdate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.DATE, -1); // 减1天
			String dd = df.format(cal.getTime());
			String nowdate = format2.format(cal.getTime());
			List<List<Object>> li = new ArrayList<List<Object>>();
			List<String> lname = new ArrayList<String>();
			List<String> ltotal = new ArrayList<String>();
			for (int i = 0; i < list2.size(); i++) {
				Object[] a = (Object[]) list2.get(i);
				name = a[0].toString().trim();
				total = a[1].toString();
				String hql = "from callout_result where agent_id is not null and  callout_time>'" + dd
						+ "' and  script_name like '%" + name + "%'";
				List<Object> list = daos.select_list(session_system, hql);
				if (list.size() >= 1) {
					//获取脚本ID
					li.add(list);
					lname.add(name);
					ltotal.add(total);
				}
			}
			if (li.size() > 0) {
				// 生成报表
				arexcelUtil a1 = new arexcelUtil();
				a1.start(li, lname, ltotal,session_system,daos);
				// 保存报表
				task task = new task();
				task.setType("1");
				task.setCreate_time(nowdate);
				task.setName(nowdate + "外呼业务报表");
				session_system.save(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
			daos.rollback_transaction(transaction_system);
			daos.rollback_transaction(transaction_41);
			daos.close_session(session_system);
			daos.close_session(session_41);
		} finally {
			daos.commit_transaction(transaction_system);
			daos.close_session(session_system);
			daos.commit_transaction(transaction_41);
			daos.close_session(session_41);
		}
	}
}

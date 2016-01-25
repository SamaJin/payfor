package com.helphand.ccdms.thread;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.helphand.ccdms.tables.eaphelp.callout_rdlc;
import com.helphand.ccdms.tables.eaphelp.callout_result;
import com.helphand.ccdms.tables.eaphelp.callout_script_detail;
import com.helphand.ccdms.tables.eaphelp.task;

import freemarker.template.SimpleDate;

public class MyNewTask extends TimerTask {
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
		transaction_system = session_system.beginTransaction();

		session_41 = HibernateSessionSystem_hw.getSession();
		transaction_41 = session_41.beginTransaction();
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
			String sql2 = "select   b.name,a.agentid,count(*) as cc from basecalllog_"
					+ moon
					+ " a,task b where a.TASK_ID=b.id   and  a.starttime>sysdate-1 and a.devicetype=1 and b.sys_id='101' and b.finish_time>sysdate group by  b.name,a.agentid";
			List<Object> list22 = daos.select_list(session_41, sql2);
			String name, total, name2, total2;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = df.parse(newdate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.DATE, -1); // 减1天
			String dd = df.format(cal.getTime());
			String nowdate = format2.format(cal.getTime());
			List<callout_rdlc> li = new ArrayList<callout_rdlc>();
			List<String> lname = new ArrayList<String>();
			for (int i = 0; i < list2.size(); i++) {
				Object[] a = (Object[]) list2.get(i);
				name = a[0].toString().trim();
				lname.add(name);
			}

			for (int i = 0; i < list22.size(); i++) {
				Object[] a = (Object[]) list22.get(i);
				name2 = a[0].toString().trim();
				total2 = a[1].toString().trim();
				total = a[2].toString().trim();
				String hql = "from callout_result where   callout_time>'" + dd
						+ "' and  script_name like '%" + name2
						+ "%' and agent_id ='" + total2 + "'";
				List<Object> list = daos.select_list(session_system, hql);
				int ktl = 0;
				if (list.size() > 0) {
					Iterator it = list.iterator();
					while (it.hasNext()) {
						callout_result cr = (callout_result) it.next();
						String[] qn_nums = cr.getResult_describe().split(";");
						String mail = qn_nums[qn_nums.length - 1];
						if (mail.equals("同意开通") || mail.equals("同意办理")
								|| mail.equals("回访成功") || mail.equals("有意向")) {
							ktl++;
						}
					}
				}
				callout_rdlc rdlc=new callout_rdlc();
				rdlc.setScript_name(name2);
				rdlc.setAgent_id(total2);
				rdlc.setWhtotal(total);
				rdlc.setKttotal(String.valueOf(ktl));
				li.add(rdlc);
			}
			if (lname.size() > 0) {
				// 生成报表
				arexcelUtil a1 = new arexcelUtil();
				a1.start2(lname,li);
				// 保存报表
				task task = new task();
				task.setType("2");
				task.setCreate_time(nowdate);
				task.setName(nowdate + "工时报表");
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

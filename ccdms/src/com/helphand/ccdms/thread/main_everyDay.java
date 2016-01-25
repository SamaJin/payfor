package com.helphand.ccdms.thread;

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class main_everyDay extends TimerTask{
	public static void main(String[] args) throws Exception{
			System.out.println("________________________________-");
			Log log = LogFactory.getLog(thread_check_task_scan.class);
			
				log.info("=====================江西彩铃统计：开始执行");
				thread_jx_conn_rate_count.main(null);
				
				log.info("=====================贵州彩铃业绩：开始执行");
				thread_gz_pay_logs.main(null);
				
				log.info("=====================江西彩铃业绩：开始执行");
				thread_jx_consume_logs.main(null);
				
				log.info("=====================江西彩铃分地市：开始执行");
				thread_jx_ds_count.main(null);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("________________________________-");
			Log log = LogFactory.getLog(thread_check_task_scan.class);
			
				log.info("=====================江西彩铃统计：开始执行");
				thread_jx_conn_rate_count.main(null);
				
				log.info("=====================贵州彩铃业绩：开始执行");
				thread_gz_pay_logs.main(null);
				
				log.info("=====================江西彩铃业绩：开始执行");
				thread_jx_consume_logs.main(null);
				
				log.info("=====================江西彩铃分地市：开始执行");
				thread_jx_ds_count.main(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

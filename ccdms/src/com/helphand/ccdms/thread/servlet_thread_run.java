package com.helphand.ccdms.thread;

import java.io.IOException;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 启动时扫描数据库执行新任务
 */
@SuppressWarnings("serial")
public class servlet_thread_run extends HttpServlet {
	private Timer timer;
	
	public servlet_thread_run() {
		timer = new Timer();
	}
	
	@Override
	public void init() throws ServletException {
		//timer.schedule(new main_everyDay(),0,80000000);
	}

	@Override
	public void destroy() {
		timer.cancel();
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doPost(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
	}
	


}

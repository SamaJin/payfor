package com.helphand.ccdms.commons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.*;

import org.apache.struts2.*;

import com.opensymphony.xwork2.*;

public class common_tools {

	public HttpServletResponse get_http_response() {
		try {
			ActionContext context = ActionContext.getContext();
			HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
			return response;
		} catch (Exception e) {
			return null;
		}
	}

	public String get_init_param(String name) {
		try {
			return ServletActionContext.getServletContext().getInitParameter(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	// 线程等待
	public void delay(int count) {
		try {
			Thread.sleep(count);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 获取时间字符串
	public String get_time_string(Date date, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	// 获取HttpServletRequest
	public HttpServletRequest get_http_request() {
		try {
			ActionContext context = ActionContext.getContext();
			HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
			return request;
		} catch (Exception e) {
			return null;
		}
	}
	
	// 获取Httpsession
	public HttpSession get_http_session() {
		try {
			ActionContext context = ActionContext.getContext();
			HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
			return request.getSession();
		} catch (Exception e) {
			return null;
		}
	}
	
	// 为空判断
	public boolean isEmpty(String str) {
		if(str == null || "".equals(str.trim()) || "null".equals(str.trim()) 
			|| "undefined".equals(str.trim())) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 日期转换 String to Date[yyyy-MM-dd HH:mm:ss]
	 * @param date
	 * @return 
	 */
	public Date dateFormatSTD(String date) {
		if(isEmpty(date)) {
			return null;
		}
		
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date resultDate = df.parse(date);
			return resultDate;
		}catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 日期转换 Date to String[yyyy-MM-dd HH:mm:ss]
	 * @param date
	 * @return
	 */
	public String dateFormatDTS(Date date) {
		if(date == null) {
			return null;
		}
		
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String resultDate = df.format(date);
			return resultDate;
		}catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 日期转换成字符串
	 * @param date 日期
	 * @param pattern 转换规则
	 * @return
	 */
	public String dateFormatDTS(Date date, String pattern) {
		if(date == null || isEmpty(pattern)) {
			return null;
		}
		
		try {
			DateFormat df = new SimpleDateFormat(pattern);
			String resultDate = df.format(date);
			return resultDate;
		}catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 字符串转换成日期
	 * @param date 日期
	 * @param pattern 转换规则
	 * @return
	 */
	public Date dateFormatSTD(String date, String pattern) {
		if(isEmpty(date) || isEmpty(pattern)) {
			return null;
		}
		
		try {
			DateFormat df = new SimpleDateFormat(pattern);
			Date resultDate = df.parse(date);
			return resultDate;
		}catch (Exception e) {
			return null;
		}
	}
}

package com.helphand.ccdms.interceptors;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.tables.admin_user;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.*;

@SuppressWarnings("serial")
public class check_user_login implements Interceptor {
	private common_tools tools = new common_tools();
	private HttpSession http_session = null;
	private admin_user user = null;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public String intercept(ActionInvocation arg0) {
		try {
			http_session = tools.get_http_session();
			user = (admin_user) http_session.getAttribute("ADMINUSER");
			if (user != null && StringUtils.isNotBlank(user.getAdmin_user_name())) {
				return arg0.invoke();
			} else {
				HttpServletResponse response = tools.get_http_response();
				PrintWriter pw = response.getWriter();
				pw.write("timeout");
				response.flushBuffer();
				pw.close();
				return null;
			}
		} catch (Exception e) {
			return "user_not_login";
		}
	}

}

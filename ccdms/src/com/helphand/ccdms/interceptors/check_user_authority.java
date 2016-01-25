package com.helphand.ccdms.interceptors;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.helphand.ccdms.commons.common_tools;
import com.helphand.ccdms.tables.admin_module;
import com.helphand.ccdms.tables.admin_user;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

@SuppressWarnings("serial")
public class check_user_authority implements Interceptor {
	private Log log = LogFactory.getLog(check_user_authority.class);
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
	@SuppressWarnings("unchecked")
	public String intercept(ActionInvocation arg0) {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			String path = request.getRequestURI();
			log.info("IE path : " + path);
			
			if(path.indexOf("/ccdms/") != -1) {
				path = path.substring(path.indexOf("/ccdms/")+7, path.indexOf(".do"));
				http_session = tools.get_http_session();
				user = (admin_user) http_session.getAttribute("ADMINUSER");
				if(user == null) {
					log.warn("session user null");
					return "user_not_authority";
				}
				
				//用户权限
				List<Object> list_admin_module  = (List<Object>)http_session.getAttribute("USER_AUTHORITY");
				list_admin_module = (list_admin_module == null ? new ArrayList<Object>() : list_admin_module);
				if(list_admin_module.size() == 0) {
					log.warn("user authority list is empty");
				}
				for(int i=0; i<list_admin_module.size(); i++) {
					admin_module module = (admin_module)list_admin_module.get(i);
					String uri = module.getModule_uri();
					if(uri.indexOf(path) != -1) {
						return arg0.invoke();
					}
				}
				
				log.warn("user authority list not uri");
			}else {
				log.warn("uri is not /ccdms/");
			}
			return "user_not_authority";
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("user error authority");
			return "user_not_authority";
		}
	}
}

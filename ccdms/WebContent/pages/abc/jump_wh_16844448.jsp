<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.suntek.app.session.SessionRegister"%>
<%@ page import="com.suntek.app.consts.CallConst"%>
<%@ page import="java.util.*"%>
<%
		String agentId = "";
		String caller ="";
		String org_called ="";
		String uri="";
		try {
			/*Vector v_AgentInfo = (Vector)SessionRegister.getUserProperty(SessionRegister.USERTYPE_OPERATOR,SessionRegister.getUserId(request, null), CallConst.SESSION_AGENT_INFO);
			agentId= (String)v_AgentInfo.elementAt(1); //工号			
			*/
			// modified by zhouhuan, 2014/6/28
			agentId = request.getRemoteUser();
			//caller= (String)SessionRegister.getCallProperty(SessionRegister.getUserId(request, null), "DispCaller"); //主叫	
			//caller= (String)SessionRegister.getCallProperty(SessionRegister.getUserId(request, null), "Caller"); //主叫			
			//caller= (String)SessionRegister.getCallProperty(agentId, "Caller");
			//uri="http://172.1.1.4:8070/jx_cailing/init.jsp?caller_mobile="+caller+"&agent_code="+agentId+"";
			org_called= (String)SessionRegister.getCallProperty(userId,CallConst.ORG_CALLED); //原被叫	
			//uri="http://172.1.1.31:8090/eapclient/pages/agent_channel_info.jsp?agent_id="+agentId+"&origcalled="+caller+"&called=16844448";
			uri="http://172.1.1.31:8090/eapclient/pages/agent_channel_info.jsp?origcalled="+org_called+"&agent_id="+agentId+"&called=16844448";
			out.print("跳转中，请梢候。。。");
			//response.setHeader("Refresh","1;URL="+uri+"");
			response.sendRedirect(uri);
		} catch (Exception ex) {
			//throw ex;
			out.println("获取当前座席人员信息异常，请重新登录！");
			return;
		}%>	

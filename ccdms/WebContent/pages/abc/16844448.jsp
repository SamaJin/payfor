<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.suntek.app.session.SessionRegister"%>
<%@ page import="com.suntek.app.consts.CallConst"%>
<%@ page import="java.util.*"%>
<%
		String agentId = "";
		String org_called ="";
		String uri="";
		boolean result = false;
		try {
			/*Vector v_AgentInfo = (Vector)SessionRegister.getUserProperty(SessionRegister.USERTYPE_OPERATOR,SessionRegister.getUserId(request, null), CallConst.SESSION_AGENT_INFO);
			agentId= (String)v_AgentInfo.elementAt(1); //工号			
			//caller= (String)SessionRegister.getCallProperty(SessionRegister.getUserId(request, null), "DispCaller"); //主叫		
			org_called= (String)SessionRegister.getCallProperty(userId,CallConst.ORG_CALLED); //原被叫		
			*/
			agentId = request.getRemoteUser();		
			org_called = (String)SessionRegister.getCallProperty(agentId, CallConst.ORG_CALLED);
			uri="http://172.1.1.31:8090/eapclient/pages/agent_channel_info.jsp?origcalled="+org_called+"&agent_id="+agentId+"&called=16844448";			
			//out.print("跳转中，请梢候。。。 agentId="+agentId+"   org_called ="+org_called);
			//response.setHeader("refresh","1;URL="+uri);
			result = true;
		} catch (Exception ex) {
			//out.println("获取当前座席人员信息异常，请重新登录！");
			//return;
		}%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript">
function init(){
	if('<%=result%>' == true || '<%=result%>'=='true' ){
		var tempUrl = '<%=uri%>', sWidth =screen.availWidth-100,sHeight=screen.availHeight-100,
		    tempParameter ='height='+sHeight+', width='+sWidth+', top=50, left=50, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no';
		window.open(tempUrl,'NewPage',tempParameter);
	}else{
		alert("获取当前座席人员信息异常，请重新登录！");
	}
}
</script>
</head>
<body onload="init();">
</body>
</html>
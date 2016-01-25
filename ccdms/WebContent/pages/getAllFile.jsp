<%@ page language="java" contentType="text/html; charset=gb2312"%>
<%@ include file="cailingconn.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Language" content="zh-CN" />
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>Insert title here</title>
<script type="text/javascript">
function checkInfo(){
	var frm = document.getElementById("frm");
	frm.submit();
}
function cancel(){
	document.getElementById("s_agent_code").value="";
	document.getElementById("s_orig_caller_number").value="";
	document.getElementById("s_caller_number").value="";
	document.getElementById("s_orig_called_number").value="";
	document.getElementById("s_called_number").value="";
	document.getElementById("s_start_time").value="";
	document.getElementById("s_end_time").value="";
	document.getElementById("s_start_length").value="";
	document.getElementById("s_end_length").value="";
}
</script>
</head>
<body>
 <table width="100%" border="0" cellspacing="0" cellpadding="10">
 	<tr>
 		<td>
 			<form action="getAllFile.jsp" id="frm" method="post">
 			工号:<input type="text" id="s_agent_code" value="" />
 			源主叫:<input type="text" id="s_orig_caller_number" value="" />
 			主叫:<input type="text" id="s_caller_number" value="" />
 			源被叫:<input type="text" id="s_orig_called_number" value="" />
 			被叫:<input type="text" id="s_called_number" value="" />
 			时间起:<input type="text" id="s_start_time" value="" />
 			时间止:<input type="text" id="s_end_time" value="" />
 			通话时长:<input type="text" id="s_start_length" value="" />
 			至:<input type="text" id="s_end_length" value="" />
 			<input type="button" id="btnSearch" value="搜索" onclick="checkInfo()" />
 			<input type="button" id="btnCancel" value="重置" onclick="cancel()" />
 			</form>
 		</td>
 	</tr>
 	<tr>
 		<td>
 			<table width="100%" border="0" cellpadding="0" cellspacing="1" bgcolor="#AACBEE">
 				<tr>
 					<td align="center">工号</td>
 					<td align="center">文件名称</td>
 					<td align="center">源主叫</td>
 					<td align="center">主叫</td>
 					<td align="center">源被叫</td>
 					<td align="center">被叫</td>
 					<td align="center">开始时间</td>
 					<td align="center">结束时间</td>
 					<td align="center">质检</td>
 					<td align="center">操作</td>
 				</tr>
 				<%
 					String s_agent_code = request.getParameter("s_agent_code");
					String s_orig_caller_number = request.getParameter("s_orig_caller_number");
 					String s_caller_number = request.getParameter("s_caller_number");
 					String s_orig_called_number = request.getParameter("s_orig_called_number");
 					String s_called_number = request.getParameter("s_called_number");
 					String s_start_time = request.getParameter("s_start_time");
 					String s_end_time = request.getParameter("s_end_time");
 					String s_start_length = request.getParameter("s_start_length");
 					String s_end_length = request.getParameter("s_end_length");
 					
 					String sql = "select * from commons_records_list where 1=1 ";
 					if(s_agent_code != "" && s_agent_code != null){
 						sql += "and agent_code='" + s_agent_code + "' ";
 					}else{
 						sql += "and 1=2 ";
 					}
 					if(s_orig_caller_number != "" && s_orig_caller_number != null){
 						sql += "and orig_caller_number='" + s_orig_caller_number + "' ";
 					}
 					if(s_caller_number != "" && s_caller_number != null){
 						sql += "and caller_number='" + s_caller_number + "' ";
 					}
 					if(s_orig_called_number != "" && s_orig_called_number != null){
 						sql += "and orig_called_number='" + s_orig_called_number + "' ";
 					}
 					if(s_called_number != "" && s_called_number != null){
 						sql += "and called_number='" + s_called_number + "' ";
 					}
 					if(s_start_time != "" && s_start_time != null){
 						sql += "and start_time>'" + s_start_time + "' ";
 					}
 					if(s_end_time != "" && s_end_time != null){
 						sql += "and start_time<'" + s_end_time + "' ";
 					}
 					String sql_time_length = "";
 					if (s_start_length != null && s_end_length != null) {
 						sql_time_length = "and round(to_number(to_date(end_time,'yyyy-mm-dd hh24:mi:ss') - to_date(start_time,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) between "+s_start_length+" and "+s_end_length+" ";
 					}else if(s_start_length != null) {
 						sql_time_length = "and round(to_number(to_date(end_time,'yyyy-mm-dd hh24:mi:ss') - to_date(start_time,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) >="+s_start_length+" ";
 					}else if(s_end_length != null) {
 						sql_time_length = "and round(to_number(to_date(end_time,'yyyy-mm-dd hh24:mi:ss') - to_date(start_time,'yyyy-mm-dd hh24:mi:ss')) * 24 * 60 * 60) <="+s_end_length+" ";
 					}
 					sql += sql_time_length;
 					sql += "order by id desc";
 					
 					
 					ResultSet rst = stmt.executeQuery(sql);
 					while (rst.next()) {
 						String agent_code = rst.getString("agent_code");
 						agent_code = new String(agent_code.getBytes("ISO-8859-1"), "gbk");
 						String file_name = rst.getString("file_name");
 						file_name = new String(file_name.getBytes("ISO-8859-1"), "gbk");
  						String orig_caller_number = rst.getString("orig_caller_number");
  						orig_caller_number = new String(orig_caller_number.getBytes("ISO-8859-1"), "gbk");
						String caller_number = rst.getString("caller_number");
						caller_number = new String(caller_number.getBytes("ISO-8859-1"), "gbk");
 						String orig_called_number = rst.getString("orig_called_number");
 						orig_called_number = new String(orig_called_number.getBytes("ISO-8859-1"), "gbk");
  						String called_number = rst.getString("called_number");
  						called_number = new String(called_number.getBytes("ISO-8859-1"), "gbk");
						String start_time = rst.getString("start_time");
						start_time = new String(start_time.getBytes("ISO-8859-1"), "gbk");
 						String end_time = rst.getString("end_time");
 						end_time = new String(end_time.getBytes("ISO-8859-1"), "gbk");
  						String is_check = rst.getString("is_check");
  						if("1".equals(is_check)){
  							is_check = "是";
  						}else{
  							is_check = "否";
  						}
  						String voiceFile = "211.138.238.154:8090/ccdms/voice/" + file_name;
				%>
 				<tr>
 					<td><%=agent_code %></td>
 					<td><%=file_name %></td>
 					<td><%=orig_caller_number %></td>
 					<td><%=caller_number %></td>
 					<td><%=orig_called_number %></td>
 					<td><%=called_number %></td>
 					<td><%=start_time %></td>
 					<td><%=end_time %></td>
 					<td><%=is_check %></td>
 					<td><embed name="audioplayer" id="audioplayer" src="<%=voiceFile %>" type="audio/x-wav" hidden="false"  width="400" height="150"  autostart="true" loop="false" /></embed></td>
 				</tr>
 				<%
 					}	
 					rst.close();
 					rst = null;
 					stmt.close();
 					oradbconn.close();
 					oradbconn = null;
 				%>				
 			</table>
 		</td>
 	</tr>
 </table>
</body>
</html>
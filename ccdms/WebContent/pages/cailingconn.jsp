<%@ page import="java.sql.*"%> 
<%
Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();   
//String oradburl="jdbc:oracle:thin:@192.168.3.20:1521:ora9idb"; 
String oradburl="jdbc:oracle:thin:@192.168.3.17:1521:oradb"; 
 
String oradbuser="cailing"; 
String oradbpassword="12530"; 
Connection oradbconn= DriverManager.getConnection(oradburl,oradbuser,oradbpassword);   
Statement stmt=oradbconn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
 
     
%> 

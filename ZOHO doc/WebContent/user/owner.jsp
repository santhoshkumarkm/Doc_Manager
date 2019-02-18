<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Owner</title>
<script type="text/javascript" src="../first.js"></script>
<link href="../first.css" rel="stylesheet" type="text/css">
</head>
<body>
	<%@page import="java.util.ArrayList"%>
	<%@page import="java.util.List"%>
	<%@page import="com.utilities.ClientUtilities"%>
	<%
		String dir = (String) session.getAttribute("dir");
		out.print("<h2>" + "Welcome " + session.getAttribute("user") + "</h2>");
		out.print("-----------------------------------------------------------" + "<br/>");
		out.print("Current directory: " + "/" + dir + "<br>");
	%>		
		<br/><input type="button" onclick="navigate(this)" value="Open folder" />
		<input type="button" onclick="navigate(this)" value="Go back" /><br/>
	<%
		out.print("-----------------------------------------------------------" + "<br/>");
	%>
	<div align="center" style="line-height: 40px;">
		<input type="button" id="menu" onclick="navigate(this)" value="Upload file" />
		<input type="button" id="menu" onclick="navigate(this)" value="New file" />
		<input type="button" id="menu" onclick="navigate(this)" value="New folder" />
		<input type="button" id="menu" onclick="navigate(this)" value="Delete file/folder" />
		<input type="button" id="menu" onclick="navigate(this)" value="Open file" /> 
		<input type="button" id="menu" onclick="navigate(this)" value="Share my file/folder" /> 
		<input type="button" id="menu" onclick="navigate(this)" value="Access other user's shared files" />
		<input type="button" id="menu" onclick="navigate(this)" value="View my shared files and folders" />
		<input type="button" id="menu" onclick="navigate(this)" value="Remove share access" />
		<input type="button" id="menu" onclick="navigate(this)" value="Find" /> <br>
		<p id="mySpace"></p>
	</div>
	<%
		if(session.getAttribute("successState") != null && session.getAttribute("successState").equals("false")){
			session.setAttribute("successState", "null");
	%>
		<!-- out.print("<h3>" + "Error occured" + "</h3>"); -->
		<script type="text/javascript">
		alertTemp("Error Occured");
		</script>
	<% 
		} else if (session.getAttribute("successState") != null && session.getAttribute("successState").equals("true")){
			session.setAttribute("successState", "null");
	%>
		<!-- out.print("<h3>" + "Success" + "</h3>"); -->
		<script type="text/javascript">
		alertTemp("Success");
		</script>
	<%
		}
	%>
	<div></div>
</body>
</html>
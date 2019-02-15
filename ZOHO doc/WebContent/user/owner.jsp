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
		out.print("-----------------------------------------------------------" + "<br>");
		out.print("Current directory: " + "/" + dir + "<br>");
		out.print("-----------------------------------------------------------" + "<br>");
	%>
	<div align="center" style="line-height: 40px;">
		<input type="button" id="menu" onclick="navigate(this)" value="Upload file" />
		<input type="button" id="menu" onclick="location.href='';" value="New file" />
		<input type="button" id="menu" onclick="location.href='';" value="New subfolder" />
		<input type="button" id="menu" onclick="location.href='';" value="Delete file/folder" />
		<input type="button" id="menu" onclick="location.href='';" value="Open file" /> 
		<input type="button" id="menu" onclick="location.href='';" value="Open folder" /> 
		<input type="button" id="menu" onclick="location.href='';" value="Share my file/folder" /> 
		<input type="button" id="menu" onclick="location.href='';" value="Access other user's shared files" />
		<input type="button" id="menu" onclick="location.href='';" value="View my shared files and folders" />
		<input type="button" id="menu" onclick="location.href='';" value="Remove share access" />
		<input type="button" id="menu" onclick="location.href='';" value="Find" /> <br>
		<p id="mySpace"></p>
	</div>
</body>
</html>
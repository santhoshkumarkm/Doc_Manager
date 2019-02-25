<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Owner</title>
<script type="text/javascript" src="../script.js"></script>
<link href="../style.css" rel="stylesheet" type="text/css">
</head>
<body style="background-color: #D7DBDD;">
	<%@page import="java.util.ArrayList"%>
	<%@page import="java.util.List"%>
	<%@page import="com.utilities.ClientUtilities"%>
	<%
		String user = (String) session.getAttribute("user");
	%>
	<div class="topbar" id="topbar">
		<%-- <div class="username" id="username"
			style="float: right; padding: 10px 10px;">
			<h4 style="display: inline;">
				Logout(<%=user%>)
			</h4>
		</div> --%>
		<div class="container" id="root" onclick="viewFiles('<%=user%>','owner')">
			<div class="icon">
				<img src='../images/folder.png' class="icon" alt="Folder Image">
			</div>
			<div class="text">root</div>
		</div>
	</div>

	<script type="text/javascript">
		getSharedUsers();
	</script>

	<div class="main">
		<div
			style="width: auto; background-color: black; height: 100%; padding: 10px 30px 10px 30px;">
			<div id="header" align="center" style="float: left;">My Files</div>
			<div style="float: right;">
				<input type="button" onclick="goBack('<%=user%>')" value="Back">
			</div>
			<div style='clear: both'></div>
			<div id="viewbox">
				<ul style="list-style-type: none;" id="myfolderlist"></ul>
			</div>
		</div>
	</div>
</body>
</html>
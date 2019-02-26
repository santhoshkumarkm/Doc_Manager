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
<body style="background-color: black;">
	<%@page import="java.util.ArrayList"%>
	<%@page import="java.util.List"%>
	<%@page import="com.utilities.ClientUtilities"%>
	<%
		String user = (String) session.getAttribute("user");
	%>
	<div class="topbar" id="topbar">
		<div class="container" id="root"
			onclick="viewFiles('<%=user%>','owner')">
			<div class="icon">
				<img src='../images/folder.png' class="icon" alt="Folder Image">
			</div>
			<div class="text">root</div>
		</div>
	</div>
	
	<div id="topbarmenu">
	<div style="float: left; padding: 10px 10px; cursor: pointer;">
			<h4 style="display: inline; color: white; padding: 5px 10px 5px 10px; font-family: sans-serif;" onclick="logout()">
				Welcome "<%=user%>"
			</h4>
		</div>
	<div style="float: right; padding: 10px 10px; cursor: pointer;">
			<h4 style="display: inline; color: white; background-color: black; padding: 5px 10px 5px 10px; font-family: sans-serif;" onclick="logout()">
				Logout(<%=user%>)
			</h4>
		</div>
	</div>

	<div class="form-popup" id="buttonForm">
		<form class="form-container">
			<div id="selected"></div>
			<button type="button" class="rightclickmenubtn" onclick="deleteFile()">Delete</button>
			<button type="button" class="rightclickmenubtn" onclick="shareFile()">Share</button>
			<button type="button" class="rightclickmenubtn cancel"
				onclick="closeButtonForm()">Close</button>
		</form>
	</div>

	<div class="form-popup" id="containerForm">
		<form class="form-container">
			<button type="button" class="rightclickmenubtn" onclick="newFile()">New
				File</button>
			<button type="button" class="rightclickmenubtn"
				onclick="uploadFile()">Upload File</button>
			<button type="button" class="rightclickmenubtn" onclick="newFolder()">New
				Folder</button>
			<button type="button" class="rightclickmenubtn cancel"
				onclick="closeBoxForm()">Close</button>
		</form>
	</div>

	<script type="text/javascript">
		getSharedUsers();
	</script>

	<div class="main">
		<div
			style="width: auto; background-color: black; height: 100%; padding: 10px 30px 10px 30px;">
			<div id="header" align="center" style="float: left;">My Files</div>
			<div style="float: right;">
				<input type="button" onclick="goBack()" value="Back">
			</div>
			<div style='clear: both'></div>
			<div id="viewbox">
				<b>Current directory: <font color="#E74C3C"><i><span
							id="dispdir">Please select a folder</span></i></font></b>
				<ul style="list-style-type: none;" id="myfolderlist"></ul>
			</div>
			<script type="text/javascript">
				setBoxRightClick();
			</script>
		</div>
	</div>

	<div class="form-popup" id="newfolderForm" style="display: none;">
		<form class="form-container">
			Folder Name: <input type="text" id="foldername"><br> <br>
			<button type="button" class="rightclickmenubtn"
				onclick="newFolderHandler()">Proceed</button>
			<button type="button" class="rightclickmenubtn cancel"
				onclick="closeNewFolderForm()">Close</button>
		</form>
	</div>
</body>
</html>
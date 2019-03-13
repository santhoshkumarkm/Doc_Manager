<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%
	String user = (String) session.getAttribute("user");
%>
<meta charset="UTF-8">
<title>Owner</title>
<script type="text/javascript" src="../script.js"></script>
<link href="../style.css" rel="stylesheet" type="text/css">
</head>
<body style="background-color: black;" onload="setOwner('<%=user%>')"
	onhashchange="onHashChange()">
	<%@page import="java.util.ArrayList"%>
	<%@page import="java.util.List"%>

	<div class="topbar" id="topbar">
		<div class="container" id="root" onclick="viewFiles('<%=user%>')">
			<div class="icon">
				<img src='../images/folder.png' class="icon" alt="Folder Image">
			</div>
			<div class="text"><%=user%>
				(root)
			</div>
		</div>
	</div>

	<div id="topbarmenu">
		<div style="float: left; padding: 10px 10px; cursor: default;">
			<h4
				style="display: inline; color: white; padding: 5px 10px 5px 10px; font-family: sans-serif;">
				Welcome "<%=user%>"
			</h4>
		</div>
		<div style="float: right; margin: 10px; cursor: pointer;"
			onclick="logout()">
			<h4
				style="display: inline; color: white; background-color: black; padding: 5px 10px 5px 10px; font-family: sans-serif;">
				Logout</h4>
		</div>
		<div style="float: right; margin: 10px;">
			<form class="search" style="max-width: 300px">
				<input type="text" id="searchText" placeholder="Min 3 letters..." onkeypress="onKeyPress()">
				<button type="button" onclick="onKeyPress()"
					style="display: inline; color: white; background-color: black; padding: 2px 5px 2px 5px; font-family: sans-serif;">Search</button>
			</form>
		</div>
	</div>

	<div class="form-popup" id="buttonForm">
		<form class="form-container">
			<div id="selected"></div>
			<button type="button" class="rightclickmenubtn"
				onclick="deleteFile()">Delete</button>
			<button type="button" class="rightclickmenubtn" onclick="shareFile()">Share
				to others</button>
			<button type="button" class="rightclickmenubtn" onclick="viewShare()">View
				share</button>
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
			<button id="back" type="button"
				style="cursor: 'pointer'; font-size: 20px;" onclick="goBack()"
				value="Back">&#8592;</button>
			<div style='clear: both'></div>
			<div id="viewbox" onclick="closeAll()">
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

	<div class="form-popup" id="shareFileForm" style="display: none;">
		<form class="form-container">
			<div id="selectedFile"></div>
			<h3>User Names:</h3>
			<select name="readselect" id="readselect" style="width: 200px;"
				multiple>
			</select> <br> <br> <input type="radio" id="read" name="privilege"
				value="read" checked>Read<br> <input type="radio"
				id="write" name="privilege" value="write">Write <br> <br>
			<button type="button" class="rightclickmenubtn"
				onclick="shareFileHandler()">Proceed</button>
			<button type="button" class="rightclickmenubtn cancel"
				onclick="closeShareFileForm()">Close</button>
		</form>
	</div>

	<div class="form-popup" id="newfileForm" style="display: none;">
		<form class="form-container">
			File Name: <input type="text" id="filename"><br> <br>
			<button type="button" class="rightclickmenubtn"
				onclick="newFileHandler()">Proceed</button>
			<button type="button" class="rightclickmenubtn cancel"
				onclick="closeNewFileForm()">Close</button>
		</form>
	</div>

	<div class="form-popup" id="viewShareForm" style="display: none;">
		<form class="form-container">
			<div id="viewFile"></div>
			<table id="table">
				<tr>
					<th>User</th>
					<th>Privilege</th>
				</tr>
			</table>
			<button type="button" class="rightclickmenubtn cancel"
				onclick="closeViewShareForm()">Close</button>
		</form>
	</div>

	<div class="editor-popup" id="editor">
		<form class="editor-container">
			<div id="displayfilename" style="float: left; display: inline;">file
				name</div>
			<div style="float: right;">
				<button type="button" id="editbutton"
					style="color: white; background-color: blue; display: none;"
					onclick="editFile()">Edit</button>
				<button type="button" id="savebutton"
					style="color: white; background-color: green;"
					onclick="submitFile()">Save</button>
				<button type="button" style="color: white; background-color: red;"
					onclick="closeEditor()">Close</button>
			</div>
			<br> <br>
			<textarea id="textarea"
				style="width: 99.5%; height: 600px; resize: none; font-family: sans-serif;"></textarea>
		</form>
	</div>

	<div class="searchmenu" id="searchmenu">
		<button type="button" style="color: white; background-color: red; position: fixed; right: 18px;"
					onclick="closeSearchMenu()">Close</button>
		<div class="searchresult" id="searchresult" style="margin-left: 10px; list-style-type: none;">
			
		</div>
	</div>
	
</body>
</html>
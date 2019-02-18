<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Open folder</title>
<script type="text/javascript" src="../first.js"></script>
</head>
<body>
	<form action="../OpenFolderController" method="get" id="myform">
		Folder Name:	<input type="text" name="foldername" /><br /> 
		<input type=submit value="Submit"/>
	</form>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Delete File/Folder</title>
<script type="text/javascript" src="../first.js"></script>
</head>
<body>
	<form action="../DeleteFileController" method="get" id="myform">
		File Name:	<input type="text" name="filename" /><br /> 
		<input type=submit value="Submit"/>
	</form>
</body>
</html>
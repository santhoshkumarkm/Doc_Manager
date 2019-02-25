<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Upload file</title>
</head>
<body>
	<form action="../UploadFileController" method="get" id="myform">
		File url:	<input type="text" name="fileurl" /><br /> 
		File Name:	<input type="text" name="filename" /><br /> 
		<input type=submit value="Submit"/>
	</form>
</body>
</html>
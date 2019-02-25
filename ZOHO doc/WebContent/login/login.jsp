<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<link href="../first.css" rel="stylesheet" type="text/css">
</head>
<body>
	<h2 align="center">Login</h2>
	<div align="center">
	<form action="../LoginController" method="post" >
		Username: <input type="text" name="name" /><br /> <br />
		Password: <input type="password" name="password" /><br /> <br />
		<input type="submit" id="bigblackbutton" value="Login"/>
	</form>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<script type="text/javascript" src="../first.js"></script>
</head>
<body>
	<h2 align="center">Enter the details</h2>
	<form action="../LoginController" method="post" align="center" >
		Email: <input type="text" name="name" /><br /> <br />
		Password: <input type="password" name="password" /><br /> <br />
		<input type="submit" style="width: 100px" value="login"/>
	</form>
</body>
</html>
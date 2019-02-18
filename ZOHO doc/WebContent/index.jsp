<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome</title>
<link rel="stylesheet" href="WebContent/resources/css/first.css" />
</head>
<body>
	<%
		if (session.getAttribute("loginState") != null && session.getAttribute("loginState").equals("false")) {
	%>
	<h4>Error occured !</h4>

	<%
		session.setAttribute("loginState", null);
		}
	%>
	<h1 align="center">Welcome !!!</h1>
	<div align="center">
		<input type="button"
			style="-webkit-appearance: none; background-color: black; font-size: 20px; color: white; height: 50px; width: 200px"
			onclick="location.href='login/login.jsp';" value="Login" /> <br>
		<br> <input type="button"
			style="-webkit-appearance: none; background-color: black; font-size: 20px; color: white; height: 50px; width: 200px"
			onclick="location.href='login/signup.jsp';" value="SignUp" />
	</div>
</body>
</html>
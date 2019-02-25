<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Welcome</title>
<script type="text/javascript" src="first.js"></script>
<link href="first.css" rel="stylesheet" type="text/css">
</head>
<body>
<%-- 	<%
		if (session.getAttribute("loginState") != null && session.getAttribute("loginState").equals("error")) {
	%>
	<script type="text/javascript">
		alertTemp("Error Occured");
	</script>
	<%
		} else if (session.getAttribute("loginState") != null
				&& session.getAttribute("loginState").equals("success")) {
	%>
	<script type="text/javascript">
		alertTemp("Success");
	</script>
	<%
		}
		session.setAttribute("loginState", "start");
	%> --%>
	<h1 align="center">Welcome !!!</h1>
	<div align="center">
		<input type="button" id="bigblackbutton"
			onclick="location.href='login/login.jsp';" value="Login" /> <br />
		<br /> <input type="button" id="bigblackbutton"
			onclick="location.href='login/signup.jsp';" value="SignUp" />
	</div>
</body>
</html>
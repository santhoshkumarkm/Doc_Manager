<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link href="style.css" rel="stylesheet" type="text/css">
<%
	String user = (String) session.getAttribute("user");
	if(user!=null){
%>
	<script type="text/javascript">
	window.location.replace("user/owner.jsp");
	</script>
<%
	} 
%>
<meta charset="UTF-8">
<title>Welcome</title>
</head>
<body>
	<h1 align="center">Welcome !!!</h1>
	<div align="center">
		<input type="button" class="bigblackbutton"
			onclick="location.href='login/login.jsp';" value="Login" /> <br />
		<br /> <input type="button" class="bigblackbutton"
			onclick="location.href='login/signup.jsp';" value="SignUp" />
	</div>
</body>
</html>
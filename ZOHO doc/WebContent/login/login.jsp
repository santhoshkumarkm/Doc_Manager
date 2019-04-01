<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%
	String user = (String) session.getAttribute("user");
	if (user != null) {
%>
<script type="text/javascript">
	window.location.replace("../user/owner.jsp");
</script>
<%
	}
%>
<meta charset="UTF-8">
<title>Login</title>
<script type="text/javascript" src="../script.js"></script>
<link href="../style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<h2 align="center">Login</h2>
	<div align="center">
		<form id="loginForm" action="../LoginController" method="post">
			Username: <input id="username" type="text" name="name" /><br /> <br />
			Password: <input id="password" type="password" name="password" /><br />
			<br /> <input type="button" class="bigblackbutton" value="Login"
				onclick="submitLoginForm()" /><br /> <br /><input type="button"
				class="bigblackbutton" value="Back" onclick="window.location='../index.jsp';"/>
		</form>
	</div>
</body>
</html>
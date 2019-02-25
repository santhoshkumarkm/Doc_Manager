<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sign Up</title>
<link href="../first.css" rel="stylesheet" type="text/css">
</head>
<body>
	<h2 align="center">Enter the details</h2>
	<div align="center">
	<form action="../SignupController" method="post">
		Username:<input type="text" name="name" /><br /> <br /> Password:<input
			type="password" name="password" /><br /> <br /> Confirm Password:<input
			type="password" name="confirmpassword" /><br /> <br />
		<input type="submit" id="bigblackbutton" value="SignUp" />
	</form>
	</div>

</body>
</html>
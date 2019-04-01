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
<title>Sign Up</title>
<script type="text/javascript" src="../script.js"></script>
<link href="../style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div style="margin-left: 44%">
		<h2>Enter the details</h2>
		<form id="signUpForm" action="../SignupController" method="post">
			Username(unique): <input type="text" name="name" id="userName"
				onblur="checkUser()" placeholder="Min 3 letters..." /><span
				id="successCheck"></span><br /> <br /> Password: <input
				id="password" type="password" name="password"
				placeholder="Min 6 letters..." /><br /> <br /> Confirm Password: <input
				type="password" id="confirmPassword" name="confirmpassword" /><br />
			<br /> <input type="button" class="bigblackbutton" value="SignUp"
				onclick="submitSignUpForm()" /> <br /><br /> <input type="button"
				class="bigblackbutton" value="Back" onclick="window.location='../index.jsp';" />
		</form>
	</div>

</body>
</html>
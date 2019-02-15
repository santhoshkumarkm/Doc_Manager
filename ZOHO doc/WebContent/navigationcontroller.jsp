<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Navigation Controller</title>
</head>
<body>
	<%@page import="java.net.URLDecoder"%>
	<%
		String option = URLDecoder.decode(request.getParameter("option"), "UTF-8");
		if (option.equals("Upload file")) {
	%>
	<jsp:include page="options/uploadfile.jsp" />
	<%
		} 
	%>
</body>
</html>
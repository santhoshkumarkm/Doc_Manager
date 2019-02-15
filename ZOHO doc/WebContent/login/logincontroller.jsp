<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Owner</title>
</head>
<body>
	<%@page import="com.dao.LoginDao"%>
	<%
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String site;

		if (LoginDao.checkClient(name, String.valueOf(password.hashCode()))) {
			out.println("Login success !");
			session.setAttribute("user", name);
			session.setAttribute("dir", name);
			session.setAttribute("login", "true");
			LoginDao.closeConnection();
			site = "../user/owner.jsp";
			response.setStatus(response.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", site);
		} else {
			LoginDao.closeConnection();
			session.setAttribute("login", "false");
			site = "../index.jsp";
		}
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	%>
</body>
</html>
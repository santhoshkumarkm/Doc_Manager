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
	<%@page import="java.io.File"%>
	<%
		String defaultLocation = "/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/";
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String site;

		if (!LoginDao.checkClient(name, String.valueOf(password.hashCode()))) {
			LoginDao.addClient(name, String.valueOf(password.hashCode()));
			File file = new File(defaultLocation + name);
			file.mkdir();
			out.println("User creation success !");
			session.setAttribute("user", name);
			session.setAttribute("dir", name);
			LoginDao.closeConnection();
			session.setAttribute("login", "true");
			LoginDao.closeConnection();
			site = "../user/owner.jsp";
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
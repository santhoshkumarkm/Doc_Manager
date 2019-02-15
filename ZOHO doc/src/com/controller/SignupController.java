package com.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.LoginDao;

public class SignupController extends HttpServlet {
	private static final long serialVersionUID = 2L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String defaultLocation = "/Users/santhosh-pt2425/Documents/Cloud_Storage_Application/Clients/";
		HttpSession session = request.getSession();
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String site;

		if (!LoginDao.checkClient(name, String.valueOf(password.hashCode()))) {
			LoginDao.addClient(name, String.valueOf(password.hashCode()));
			File file = new File(defaultLocation + name);
			file.mkdir();
			session.setAttribute("user", name);
			session.setAttribute("dir", name);
			LoginDao.closeConnection();
			session.setAttribute("login", "true");
			LoginDao.closeConnection();
			site = "user/owner.jsp";
		} else {
			LoginDao.closeConnection();
			session.setAttribute("login", "false");
			site = "index.jsp";
		}
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	}
}
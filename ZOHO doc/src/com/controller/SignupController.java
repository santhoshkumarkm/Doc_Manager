package com.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
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
		ServletContext context = getServletContext();
		String defaultLocation = context.getInitParameter("defaultLocation");
		HttpSession session = request.getSession();
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmpassword");
		String site;

		if (name.length() > 0 && password.length() > 0 && confirmPassword.length() > 0
				&& password.equals(confirmPassword)
				&& !LoginDao.checkClient(name, String.valueOf(password.hashCode()))) {
			LoginDao.addClient(name, String.valueOf(password.hashCode()));
			File file = new File(defaultLocation + name);
			file.mkdir();
			session.setAttribute("user", name);
			site = "user/owner.jsp";
		} else {
			site = "index.jsp";
		}
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	}
}
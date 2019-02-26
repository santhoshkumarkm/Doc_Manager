package com.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.LoginDao;

public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String site;
		if (name.length() > 0 && password.length() > 0
				&& LoginDao.checkClient(name, String.valueOf(password.hashCode()))) {
			session.setAttribute("user", name);
			session.setAttribute("loginState", "success");
			site = "user/owner.jsp";
		} else {
			session.setAttribute("loginState", "error");
			site = "index.jsp";
		}
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	}
}

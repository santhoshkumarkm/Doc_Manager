package com.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;
import com.dao.LoginDao;

public class SignupController extends HttpServlet {
	private static final long serialVersionUID = 2L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmpassword");
		String site;

		if (name.length() > 0 && password.length() > 0 && confirmPassword.length() > 0
				&& password.equals(confirmPassword)
				&& !LoginDao.checkClient(name, String.valueOf(password.hashCode()))) {
			if(LoginDao.addClient(name, String.valueOf(password.hashCode()))) {
				File file = new File(defaultLocation + name);
				file.mkdir();
				session.setAttribute("user", name);
				site = "user/owner.jsp";				
			}else {				
				site = "index.jsp";
			}
		} else {
			site = "index.jsp";
		}
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	}
}
package com.controller;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NavigationController extends HttpServlet {
	private static final long serialVersionUID = 3L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String option = URLDecoder.decode(request.getParameter("option"), "UTF-8");
		if (option.equals("Upload file")) {
			
		}
	}
}
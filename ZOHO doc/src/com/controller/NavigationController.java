package com.controller;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.RequestDispatcher;
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
			RequestDispatcher rd = request.getRequestDispatcher("options/uploadfile.jsp");
			rd.include(request, response);
		} else if (option.equals("New folder")) {
			RequestDispatcher rd = request.getRequestDispatcher("options/newfolder.jsp");
			rd.include(request, response);
		} else if (option.equals("Delete file/folder")) {
			RequestDispatcher rd = request.getRequestDispatcher("options/deletefile.jsp");
			rd.include(request, response);
		} else if (option.equals("Open folder")) {
			RequestDispatcher rd = request.getRequestDispatcher("options/openfolder.jsp");
			rd.include(request, response);
		} else if (option.equals("Share my file/folder")) {
			RequestDispatcher rd = request.getRequestDispatcher("options/sharefile.jsp");
			rd.include(request, response);
		}
	}
}
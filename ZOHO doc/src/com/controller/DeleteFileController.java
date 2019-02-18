package com.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.ClientsInfoDao;

public class DeleteFileController extends HttpServlet {
	private static final long serialVersionUID = 5L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String fileName = request.getParameter("filename");
		String location = session.getAttribute("dir") + "/" + fileName;
		File file = new File(defaultLocation + "/" + location);
		if (file.exists()) {
			file.delete();
			ClientsInfoDao.deleteFile(location);
			session.setAttribute("successState", "true");
		} else {
			session.setAttribute("successState", "false");
		}
		String site = "user/owner.jsp";
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	}

}
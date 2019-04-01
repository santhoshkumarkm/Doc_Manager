package com.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;

@WebServlet("/NewFolderController")
public class NewFolderController extends HttpServlet {
	private static final long serialVersionUID = 5L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sessionUser = (String) session.getAttribute("user");
		String folderName = request.getParameter("foldername");
		String location = request.getParameter("location") + "/" + folderName;
		JSONObject jsonObject = new JSONObject();
		String successState = "ERROR";
		File file = new File(defaultLocation + location);
		boolean flag = false;
		String checkLocation = location.substring(0, location.indexOf('/'));
		if (checkLocation.equals(sessionUser)) {
			flag = true;
		} else {
				long fileId = ClientsInfoDao.getFileId(request.getParameter("location"));
				String privilegeInfo = ClientsInfoDao.checkLocation(fileId, sessionUser);
				if (privilegeInfo.substring(0, privilegeInfo.indexOf('+')).equals("write")) {
					flag = true;
				}
		}
		if (flag && !file.exists()) {			
			file.mkdir();
			ClientsInfoDao.insertFile(location);
			successState = "true";
		}
		jsonObject.put("success", successState);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}
}

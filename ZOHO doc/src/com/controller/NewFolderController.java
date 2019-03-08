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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String folderName = request.getParameter("foldername");
		String location = request.getParameter("location") + "/" + folderName;
		JSONObject jsonObject = new JSONObject();
		String successState = "false";
		File file = new File(defaultLocation + location);
		if (!file.exists()) {			
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

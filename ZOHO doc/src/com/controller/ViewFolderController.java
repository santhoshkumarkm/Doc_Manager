package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;

public class ViewFolderController extends HttpServlet {
	private static final long serialVersionUID = 9L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sharedUser = URLDecoder.decode(request.getParameter("shareduser"), "UTF-8");
		String userName = (String) session.getAttribute("user");
		Map<String, String> crunchifyMap = new LinkedHashMap<String, String>();
		if (sharedUser.equals(userName)) {
			crunchifyMap.put(userName, "owner");
		} else {
			crunchifyMap = ClientsInfoDao.getSharedFilesForAnUser(sharedUser, userName);
		}
		JSONObject jsonObject = new JSONObject(crunchifyMap);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();

	}

}
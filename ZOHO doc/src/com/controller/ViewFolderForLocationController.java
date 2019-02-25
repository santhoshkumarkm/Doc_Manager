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

public class ViewFolderForLocationController extends HttpServlet {
	private static final long serialVersionUID = 10880L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String location = URLDecoder.decode(request.getParameter("location"), "UTF-8");
		System.out.println(location);
		String privilege = URLDecoder.decode(request.getParameter("privilege"), "UTF-8");
		Map<String, String> crunchifyMap = new LinkedHashMap<String, String>();
		crunchifyMap = ClientsInfoDao.getSharedFilesForALocation(location, privilege);
		JSONObject jsonObject = new JSONObject(crunchifyMap);
		session.setAttribute("successState", "success");
		session.setAttribute("dir", location);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();

	}

}

package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;

@WebServlet("/ViewShareController")
public class ViewShareController extends HttpServlet {
	private static final long serialVersionUID = 50121L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sessionUser = (String) session.getAttribute("user");
		String location = request.getParameter("location");
		Map<String, String> crunchifyMap = new LinkedHashMap<String, String>();
		String successState = "ERROR";
		String checkLocation = location.substring(0, location.indexOf('/'));
		JSONObject jsonObject = null;
		if (checkLocation.equals(sessionUser)) {
			crunchifyMap = ClientsInfoDao.sharedUsersForAFile(location);
			jsonObject = new JSONObject(crunchifyMap);
			successState = "true";
		}
		if (jsonObject == null) {
			jsonObject = new JSONObject();
			jsonObject.put("success", successState);
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}
}
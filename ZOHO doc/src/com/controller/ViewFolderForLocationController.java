package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
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

@WebServlet("/ViewFolderForLocationController")
public class ViewFolderForLocationController extends HttpServlet {
	private static final long serialVersionUID = 10880L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String location = URLDecoder.decode(request.getParameter("location"), "UTF-8");
		Map<String, String> crunchifyMap = new LinkedHashMap<String, String>();
		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("user");
		if (location.startsWith(userName)) {
			crunchifyMap = ClientsInfoDao.getRootUserFiles(location);
			session.setAttribute("privilege", "default");
			System.out.println("privilege set at for location in root user");
		} else {
			crunchifyMap = ClientsInfoDao.getSharedFilesForALocation(location, userName);
			if (crunchifyMap.containsValue("read")) {
				session.setAttribute("privilege", "read");
			} else {
				session.setAttribute("privilege", "write");
			}
		}
		JSONObject jsonObject = new JSONObject(crunchifyMap);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();

	}

}

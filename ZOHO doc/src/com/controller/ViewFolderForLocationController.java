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
		String location = request.getParameter("location");
		Map<String, String> crunchifyMap = new LinkedHashMap<String, String>();
		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("user");
		JSONObject jsonObject = null;
		System.out.println(location + "@" + ClientsInfoDao.getFileId(location));
		if (location.indexOf('/') != -1 && ClientsInfoDao.getFileId(location) != 0) {
			if (location.startsWith(userName)) {
				crunchifyMap = ClientsInfoDao.getRootUserFiles(location);
			} else {
				crunchifyMap = ClientsInfoDao.getSharedFilesForALocation(location);
			}
			jsonObject = new JSONObject(crunchifyMap);
		} else {
			jsonObject = new JSONObject();
			jsonObject.put("success", "ERROR");
		}
		System.out.println("json: " + jsonObject);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();

	}

}

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

@WebServlet("/GoBackController")
public class GoBackController extends HttpServlet {
	private static final long serialVersionUID = 139L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String currentDir = request.getParameter("location");
		String rootUser = (String) session.getAttribute("user");
		JSONObject jsonObject = new JSONObject();
		String prevDir = currentDir, access = "denied";
		if (prevDir.indexOf('/') != -1) {
			if (currentDir.startsWith(rootUser)) {
				prevDir = currentDir.substring(0, currentDir.lastIndexOf('/'));
				access = "owner";
			} else {
				prevDir = currentDir.substring(0, currentDir.lastIndexOf('/'));
				String sharedUser = currentDir.substring(0, currentDir.indexOf('/'));
				access = ClientsInfoDao.checkAccess(prevDir, rootUser);
//				System.out.println("access: "+ access);
				if (access.equals("denied")) {
					prevDir = sharedUser;
//					System.out.println(prevDir);
					access = "default";
				}
			}
		}
		jsonObject.put("access", access);
		jsonObject.put("prevLocation", prevDir);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();

	}

}

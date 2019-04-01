package com.controller;

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

@WebServlet("/ValidateController")
public class ValidateController extends HttpServlet {
	private static final long serialVersionUID = 5021L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		JSONObject jsonObject = new JSONObject();
		String successState = "false";
		String location = request.getParameter("location");
		String privilege = null;
		String sessionUser = (String) session.getAttribute("user");
		if (sessionUser != null && location != null) {
			if (location.startsWith(sessionUser)) {
				successState = "true";
				privilege = "owner";
			} else if (location.indexOf('/') == -1) {
				successState = "true";
				privilege = "read";
			} else {
				long fileId = ClientsInfoDao.getFileId(location);
				String privilegeInfo = ClientsInfoDao.checkLocation(fileId, sessionUser);
				if (privilegeInfo != null) {
					privilegeInfo = privilegeInfo.substring(0, privilegeInfo.indexOf('+'));
					if (privilegeInfo.equals("write") || privilegeInfo.equals("read")) {
						privilege = privilegeInfo;
						successState = "true";
					}
				}
			}
		}
//		System.out.println(successState+user+sessionUser);
		jsonObject.put("success", successState);
		jsonObject.put("privilege", privilege);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}
}
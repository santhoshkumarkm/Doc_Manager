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
import com.utilities.FileOperations;

@WebServlet("/OpenFileController")
public class OpenFileController extends HttpServlet {
	private static final long serialVersionUID = 5021L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("user");
		String filePath = request.getParameter("filename");
		JSONObject jsonObject = new JSONObject();
		String successState = "false", content = "";
		FileOperations file = new FileOperations(filePath);
		String privilege = null;
		if (file.exists()) {
			content = file.read();
			privilege = ClientsInfoDao.checkLocation(ClientsInfoDao.getFileId(filePath), userName);
			if (privilege != null) {
				privilege = privilege.substring(0, privilege.indexOf('+'));
			}
			successState = "true";
		}
//		System.out.println(successState + content + privilege);
		jsonObject.put("success", successState);
		jsonObject.put("content", content);
		jsonObject.put("privilege", privilege);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}
}
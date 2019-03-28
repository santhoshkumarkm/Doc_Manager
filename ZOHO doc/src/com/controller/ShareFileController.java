package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;

@WebServlet("/ShareFileController")
public class ShareFileController extends HttpServlet {
	private static final long serialVersionUID = 81423L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sessionUser = (String) session.getAttribute("user");
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String[] users = reader.readLine().split(",");
		String privilege = request.getParameter("privilege");
		String location = request.getParameter("location");
		JSONObject jsonObject = new JSONObject();
		String successState = "ERROR";
		boolean flag = false;
		String checkLocation = location.substring(0, location.indexOf('/'));
		if (checkLocation.equals(sessionUser)) {
			flag = true;
		}
		if (flag) {
			boolean flag2 = true;
			for (String user : users) {
				successState = "true";
				String tempState = (String) ClientsInfoDao.shareFile(user, location, privilege).get("success");
				if (tempState.equals("false")) {
					successState = "ERROR";
					flag2 = false;
				}
			}
			if (flag2) {
				successState = "true";
			}
		}
		jsonObject.put("success", successState);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}

}

package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;

public class SharedUserListController extends HttpServlet {
	private static final long serialVersionUID = 10L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		JSONObject jsonObject = new JSONObject();
		JSONArray userListArray = new JSONArray();
		String userName = (String) session.getAttribute("user");
		LinkedHashSet<String> userList = ClientsInfoDao.getSharedUserNamesForAnUser(userName);
		for (String s : userList) {
			userListArray.add(s);
		}
		jsonObject.put("userList", userListArray);
		session.setAttribute("successState", "success");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}

}

package com.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.dao.LoginDao;

@WebServlet("/DuplicateUserController")
public class DuplicateUserController extends HttpServlet {
	private static final long serialVersionUID = 98L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userName = request.getParameter("username");
		JSONObject jsonObject = new JSONObject();
		String successCheck = "false";
		if (!LoginDao.checkClientName(userName)) {
			successCheck = "true";
		}
		jsonObject.put("success", successCheck);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}
}
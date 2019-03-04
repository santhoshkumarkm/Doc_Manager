package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

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
		String fileName = request.getParameter("filename");
		JSONObject jsonObject = new JSONObject();
		String successState = "false", content="";
		File file = new File(defaultLocation + fileName);
		if (file.exists()) {
			content= stringBuilder(new BufferedReader(new FileReader(file)));
			successState = "true";
		}
		jsonObject.put("success", successState);
		jsonObject.put("content", content);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}
	
	private static String stringBuilder(BufferedReader bin) {
		StringBuilder stringBuilder = new StringBuilder();
		String s = "";
		try {
			while ((s = bin.readLine()) != null) {
				stringBuilder.append(s + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}
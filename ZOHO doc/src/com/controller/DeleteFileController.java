package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;
import com.utilities.AddWordsTask;
import com.utilities.Utilities;

@WebServlet("/DeleteFileController")
public class DeleteFileController extends HttpServlet {
	private static final long serialVersionUID = 7L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String location = request.getParameter("location");
		File file = new File(defaultLocation + "/" + location);
		JSONObject jsonObject = new JSONObject();
		String successState = "false";
		if (file.exists()) {
			LinkedHashMap<Integer, String>[] twoLists = Utilities
					.getEditedWords(Utilities.stringBuilder(new BufferedReader(new FileReader(file))), "");
			AddWordsTask.getEditList().addFileName(ClientsInfoDao.getFileId(location)+"+"+location, twoLists);
			ClientsInfoDao.deleteFile(location);
			successState = "true";
		}
		jsonObject.put("success", successState);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}

}
package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;
import com.utilities.AddWordsTask;
import com.utilities.FileOperations;
import com.utilities.Utilities;

@WebServlet("/DeleteFileController")
public class DeleteFileController extends HttpServlet {
	private static final long serialVersionUID = 7L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sessionUser = (String) session.getAttribute("user");
		String location = request.getParameter("location");
		FileOperations file = new FileOperations(location);
		JSONObject jsonObject = new JSONObject();
		String successState = "ERROR";
		if (location.startsWith(sessionUser) && file.exists()) {
			if (location.endsWith(".txt")) {
				LinkedHashMap<Integer, String>[] twoLists = Utilities
						.getEditedWords(file.read(), "");
				AddWordsTask.getEditList().addFileName(ClientsInfoDao.getFileId(location) + "+" + location, twoLists);
				ClientsInfoDao.deleteFile(location);
				successState = "true";
			} else {
				LinkedList<String> fileNames = ClientsInfoDao.getFilesInDirectory(location);
//				System.out.println("file names: " + fileNames);
				for (String filePath : fileNames) {
					File subFile = new File(defaultLocation + "/" + filePath);
//					System.out.println("file: " + subFile);
					LinkedHashMap<Integer, String>[] twoLists = Utilities
							.getEditedWords(Utilities.stringBuilder(new BufferedReader(new FileReader(subFile))), "");
					AddWordsTask.getEditList().addFileName(ClientsInfoDao.getFileId(filePath) + "+" + location,
							twoLists);
				}
				ClientsInfoDao.deleteFile(location);
//				System.out.println("file: " + file);
				successState = "true";
			}
			file.delete();
		}
		jsonObject.put("success", successState);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}

}
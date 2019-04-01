package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

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

@WebServlet("/NewFileController")
public class NewFileController extends HttpServlet {
	private static final long serialVersionUID = 501L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sessionUser = (String) session.getAttribute("user");
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
//		String fileName = request.getParameter("filename");
		String mode = request.getParameter("mode");
		String text = Utilities.stringBuilder(reader);
		String location = request.getParameter("filename");
		JSONObject jsonObject = new JSONObject();
		String successState = "false";
		FileOperations file = new FileOperations(location);
		boolean flag = false;
		if (location.startsWith(sessionUser)) {
			flag = true;
		} else {
			long fileId = 0;
			if (mode.equals("new")) {
				fileId = ClientsInfoDao.getFileId(location.substring(0, location.lastIndexOf('/')));
			} else {
				fileId = ClientsInfoDao.getFileId(location);
			}
//			System.out.println(fileId+ sessionUser);
			String privilegeInfo = ClientsInfoDao.checkLocation(fileId, sessionUser);
//			System.out.println("privilege:" + privilegeInfo);
			if (privilegeInfo != null && privilegeInfo.substring(0, privilegeInfo.indexOf('+')).equals("write")) {
				flag = true;
			}
		}
//		System.out.println(file);
		if (flag) {
			if (!file.exists() && mode.equals("new")) {
				ClientsInfoDao.insertFile(location);
				AddWordsTask.getFileList().addFileName(location);
			} else if (file.exists() && mode.equals("edit")) {
				LinkedHashMap<Integer, String>[] twoLists = Utilities
						.getEditedWords(file.read(), text);
//				System.out.println(Arrays.toString(twoLists));
				AddWordsTask.getEditList().addFileName(ClientsInfoDao.getFileId(location) + "+" + location, twoLists);
				file.delete();
				ClientsInfoDao.insertFile(location);
			}
			file.create();
			file.write(text);
			successState = "true";
		}
		jsonObject.put("success", successState);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}

}
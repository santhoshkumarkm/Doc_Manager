package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
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
		String fileName = request.getParameter("filename");
		String mode = request.getParameter("mode");
		String text = Utilities.stringBuilder(reader);
		String location = request.getParameter("location") + "/" + fileName;
		JSONObject jsonObject = new JSONObject();
		String successState = "false";
		File file = new File(defaultLocation + location);
		boolean flag = false;
		String checkLocation = location.substring(0, location.indexOf('/'));
		if (checkLocation.equals(sessionUser)) {
			flag = true;
		} else {
			long fileId = ClientsInfoDao.getFileId(request.getParameter("location"));
			String privilegeInfo = ClientsInfoDao.checkLocation(fileId, sessionUser);
			if (privilegeInfo.substring(0, privilegeInfo.indexOf('+')).equals("write")) {
				flag = true;
			}
		}
		System.out.println(file);
		if (flag) {
			if (!file.exists() && mode.equals("new")) {
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				fw.write(text);
				fw.close();
				ClientsInfoDao.insertFile(location);
				successState = "true";
				AddWordsTask.getFileList().addFileName(location);
			} else if (file.exists() && mode.equals("edit")) {
				LinkedHashMap<Integer, String>[] twoLists = Utilities
						.getEditedWords(Utilities.stringBuilder(new BufferedReader(new FileReader(file))), text);
				System.out.println(Arrays.toString(twoLists));
				AddWordsTask.getEditList().addFileName(ClientsInfoDao.getFileId(location) + "+" + location, twoLists);
				file.delete();
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				fw.write(text);
				fw.close();
				ClientsInfoDao.insertFile(location);
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
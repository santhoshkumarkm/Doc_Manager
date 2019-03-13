package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String fileName = request.getParameter("filename");
		String mode = request.getParameter("mode");
		String text = Utilities.stringBuilder(reader);
		String location = request.getParameter("location") + "/" + fileName;
		JSONObject jsonObject = new JSONObject();
		String successState = "false";
		File file = new File(defaultLocation + location);
		if (!file.exists() && mode.equals("new")) {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(text);
			fw.close();
			ClientsInfoDao.insertFile(location);
			successState = "true";
			AddWordsTask.getFileList().addFileName(location);
		} else if (file.exists() && mode.equals("edit")) {
			LinkedHashMap<Integer, String>[] twoLists = getEditedWords(
					Utilities.stringBuilder(new BufferedReader(new FileReader(file))), text);
			AddWordsTask.getEditList().addFileName(location, twoLists);
			file.delete();
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(text);
			fw.close();
			ClientsInfoDao.insertFile(location);
			successState = "true";
		}
		jsonObject.put("success", successState);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonObject);
		out.flush();
	}

	private LinkedHashMap<Integer, String>[] getEditedWords(String prevFile, String text) {
		LinkedHashMap<Integer, String> prevList = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> updatedList = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String>[] twoLists = new LinkedHashMap[2];
		String[] prev = prevFile.split("\\W+");
		String[] updated = text.split("\\W+");
		int count = 0;
		for (count = 0; count < updated.length; count++) {
			if (prev[count] != null && !updated[count].equals(prev[count])) {
				prevList.put(count, prev[count]);
				updatedList.put(count, updated[count]);
			} else {
				updatedList.put(count, updated[count]);
			}
		}
		count--;
		while (count < prev.length) {
			prevList.put(count, prev[count]);
			count++;
		}
			twoLists[0] = prevList;
		twoLists[1] = updatedList;
		return twoLists;
	}
}
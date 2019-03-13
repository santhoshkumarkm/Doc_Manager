package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;
import com.utilities.HashMapUtil;
import com.utilities.Utilities;

@WebServlet("/SearchController")
public class SearchController extends HttpServlet {
	private static final long serialVersionUID = 5021L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String user = (String) session.getAttribute("user");
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		JSONObject jsonObject = new JSONObject();
		String successState = "true";
		String words = Utilities.stringBuilder(reader);
		System.out.println("words: " + words);
		LinkedHashMap<Integer, ArrayList<Integer>> wordDetailMap = new HashMapUtil().findWord(words.split("\\W+"));
		System.out.println("word detail: " + wordDetailMap);
		if (wordDetailMap != null) {
			LinkedHashMap<String, Integer> checkedMap = ClientsInfoDao.search(wordDetailMap, user);
			if (checkedMap.size() > 0) {
				for (Map.Entry<String, Integer> entry : checkedMap.entrySet()) {
					jsonObject.put(entry.getKey(), entry.getValue());
				}
			} else {
				successState = "false";
			}
		} else {
			successState = "false";
		}
		jsonObject.put("success", successState);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		System.out.println(jsonObject);
		out.print(jsonObject);
		out.flush();
	}
}
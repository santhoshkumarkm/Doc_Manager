package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.dao.ClientsInfoDao;
import com.utilities.AddWordsTask;
import com.utilities.Utilities;

@WebServlet("/SearchController")
public class SearchController extends HttpServlet {
	private static final long serialVersionUID = 5021L;
	String defaultLocation;
	String[] commonWords = { "the", "and", "that", "have", "for", "not", "with", "you", "this", "but", "his", "from",
			"they", "her", "she", "will", "would", "there", "their", "your", "could", "also" };

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
		String words = Utilities.stringBuilder(reader);
//		System.out.println("words: " + words);
		JSONArray jsonFinalArray = new JSONArray();
		PrintWriter out = null;
		boolean common = false;
		for (String commonWord : commonWords) {
//			System.out.println("word: " + words.trim());
			if (words.trim().equals(commonWord)) {
				jsonObject = new JSONObject();
				jsonObject.put("common", "found");
				common = true;
				break;
			}
		}
		if (common) {
			out = response.getWriter();
			out.print(jsonObject);
//			System.out.println("json: " + jsonObject);
		}
		if (!common) {
			LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> wordsDetailMap = AddWordsTask
					.getHashMapUtil().findWord(words.split("\\W+"));
//		System.out.println("before word detail: " + wordsDetailMap);
			if (wordsDetailMap == null || wordsDetailMap.size() == 0) {
				wordsDetailMap = AddWordsTask.getHashMapUtil().editDistance(words.split("\\W+"));
				if (wordsDetailMap != null && wordsDetailMap.size() > 0) {
					JSONObject tempJsonObject = new JSONObject();
					tempJsonObject.put("editDistance", null);
					jsonFinalArray.add(tempJsonObject);
				}
			}
//		System.out.println("after word detail: " + wordsDetailMap);
			if (wordsDetailMap != null && wordsDetailMap.size() > 0) {
				for (Map.Entry<String, LinkedHashMap<Integer, ArrayList<Integer>>> entry : wordsDetailMap.entrySet()) {
					JSONArray jsonArray = ClientsInfoDao.search(entry.getValue(), user);
					if (jsonArray.size() > 0) {
						jsonObject.put(entry.getKey(), jsonArray);
					}
//				System.out.println("json: " +jsonObject);
				}
			}
			if (jsonObject.size() > 0) {
				Set<Entry<String, JSONArray>> set = jsonObject.entrySet();
				List<Entry<String, JSONArray>> list = new ArrayList<Entry<String, JSONArray>>(set);
				if (list.size() > 1) {
					Collections.sort(list, new Comparator<Map.Entry<String, JSONArray>>() {
						public int compare(Map.Entry<String, JSONArray> o1, Map.Entry<String, JSONArray> o2) {
							return (Integer.valueOf(o2.getValue().size()))
									.compareTo(Integer.valueOf(o1.getValue().size()));
						}
					});
				}
				for (Map.Entry<String, JSONArray> aa : list) {
					jsonObject = new JSONObject();
					jsonObject.put(aa.getKey(), aa.getValue());
					jsonFinalArray.add(jsonObject);
				}
			}
			out = response.getWriter();
			out.print(jsonFinalArray);
		}
//		System.out.println("array:" + jsonFinalArray);
		response.setContentType("application/json");
		out.flush();
	}
}
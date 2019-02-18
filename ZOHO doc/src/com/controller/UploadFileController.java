package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.ClientsInfoDao;

public class UploadFileController extends HttpServlet {
	private static final long serialVersionUID = 4L;
	String defaultLocation;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		defaultLocation = getServletContext().getInitParameter("defaultLocation");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String fileUrl = request.getParameter("fileurl"), fileName = request.getParameter("filename");
		File clientFile = new File(fileUrl);
		StringBuilder stringBuilder = new StringBuilder();
		if (clientFile.exists()) {
			BufferedReader bin = new BufferedReader(new FileReader(clientFile));
			String s = "";
			while ((s = bin.readLine()) != null) {
				stringBuilder.append(s + "\n");
			}
			bin.close();
			String content = stringBuilder.toString(),
					location = session.getAttribute("dir") + "/" + fileName + ".txt";
			File newFile = new File(defaultLocation + "/" + location);
			newFile.createNewFile();
			FileWriter fw = new FileWriter(newFile);
			fw.write(content);
			fw.close();
			ClientsInfoDao.insertFile(location);
			session.setAttribute("successState", "true");
		} else {
			session.setAttribute("successState", "false");
		}
		String site = "user/owner.jsp";
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	}

}

package com.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dao.ClientsInfoDao;

@WebServlet("/UploadFileController")
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
		String sessionUser = (String) session.getAttribute("user");
		String fileUrl = request.getParameter("fileurl"), fileName = request.getParameter("filename");
		File clientFile = new File(fileUrl);
		StringBuilder stringBuilder = new StringBuilder();
		boolean flag = false;
		if (request.getParameter("fileurl").equals(sessionUser)) {
			flag = true;
		} else {
				long fileId = ClientsInfoDao.getFileId(request.getParameter("fileurl"));
				if(ClientsInfoDao.checkLocation(fileId, sessionUser).equals("write")) {
					flag = true;
				}
		}
		if (flag && clientFile.exists()) {
			BufferedReader bin = new BufferedReader(new FileReader(clientFile));
			String s = "";
			while ((s = bin.readLine()) != null) {
				stringBuilder.append(s + "\n");
			}
			bin.close();
			String content = stringBuilder.toString(),
					location = fileUrl + "/" + fileName + ".txt";
			File newFile = new File(defaultLocation + "/" + location);
			newFile.createNewFile();
			FileWriter fw = new FileWriter(newFile);
			fw.write(content);
			fw.close();
			ClientsInfoDao.insertFile(location);
			session.setAttribute("successState", "success");
		} else {
			session.setAttribute("successState", "error");
		}
		String site = "user/owner.jsp";
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", site);
	}

}

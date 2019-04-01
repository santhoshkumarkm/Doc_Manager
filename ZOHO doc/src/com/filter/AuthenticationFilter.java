package com.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

@WebFilter(urlPatterns = { "/user/owner", "/SharedUserListController", "/AllUserListController",
		"/ChangePrivilegeController", "/DeleteFileController", "/GoBackController", "/NewFileController",
		"/NewFolderController", "/OpenFileController", "/SharedUserListController", "/ShareFileController",
		"/SignUpController", "/UploadFileController", "/ViewFolderController", "/ViewFolderForLocationController",
		"/ViewShareController" })
public class AuthenticationFilter implements Filter {

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpSession session = request.getSession();
//		System.out.println("Do Filter called");
//		System.out.println(request.getServletPath());
		String user = (String) session.getAttribute("user");
//		System.out.println(user);
		if (user != null) {
			chain.doFilter(req, resp);// sends request to next resource
		} else {
			session.invalidate();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("success", "logout");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonObject);
			out.flush();
		}

	}

	public void destroy() {
	}

}
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

@WebFilter(urlPatterns = { "/NewFolderController", "/NewFileController", "/ShareFileController", "/ViewShareController",
		"/UploadFileController", "/DeleteFileController", "/ChangePrivilegeController" })
public class PrivilegeFilter implements Filter {

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpSession session = request.getSession();
//		System.out.println("Privilege Filter called");
//		System.out.println(request.getServletPath());
		String privilege = (String) session.getAttribute("privilege");
//		System.out.println(privilege);
		if (!privilege.equals("read")) {
			chain.doFilter(req, resp);// sends request to next resource
		} else {
			session.invalidate();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("success", "false");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(jsonObject);
			out.flush();
		}

	}

	public void destroy() {
	}

}
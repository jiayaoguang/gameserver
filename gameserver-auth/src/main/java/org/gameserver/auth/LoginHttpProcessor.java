package org.gameserver.auth;

import java.util.Map;

import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class LoginHttpProcessor extends HttpProcessor{

	@Override
	public void service(Request request, Response response) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		System.out.println(request.getParameters());
		
		response.write("<html><head></head><body>welcome to index<body></html>".getBytes());
		
	}

}


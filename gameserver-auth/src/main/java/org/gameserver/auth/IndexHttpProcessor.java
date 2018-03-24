package org.gameserver.auth;

import java.util.Map;

import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class IndexHttpProcessor extends HttpProcessor{

	@Override
	public void service(Request request, Response response) {
		response.write("<html><head></head><body>welcome user "+ request.getParameter("username") +" to index<body></html>");
	}

}


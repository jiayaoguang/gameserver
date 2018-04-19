package org.gameserver.auth.processor;

import java.util.Set;

import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.util.TokenUtil;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class LoginHttpProcessor extends HttpProcessor{
	
	@Override
	public void service(Request request, Response response) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println(username + " >> " + password);
		
		request.decodeCookies();
		
		Set<Cookie> cookies = request.decodeCookies();
		for(Cookie c: cookies) {
			
			System.out.println(c.name() + " : " + c.value());
		}
		Cookie cookie = new DefaultCookie("jyg", "jia");
		cookie.setMaxAge(60*60);
//		cookie.setDomain("127.0.0.1");
		cookie.setPath("/");
		response.addCookie(cookie);
		
		if("admin".equals(username)) {
			response.writeAndFlush("<html><head></head><body>welcome user "+ request.getParameter("username") +" to index,"+ " token :" +TokenUtil.getToken()+"<body></html>");
//			response.setContentType(Response.CONTENT_TYPE_JSON);
			
			String json = "{"+"";
			
//			getDispatcher("/index").service(request, response);
		}else {
			getDispatcher("/404").service(request, response);
		}
		
	}

}


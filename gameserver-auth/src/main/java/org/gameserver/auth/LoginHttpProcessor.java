package org.gameserver.auth;

import java.util.Set;

import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;

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
			
			System.out.println(c.maxAge());
		}
		if("admin".equals(username)) {
			getDispatcher("/index").service(request, response);
		}else {
			getDispatcher("/404").service(request, response);
		}
		
	}

}


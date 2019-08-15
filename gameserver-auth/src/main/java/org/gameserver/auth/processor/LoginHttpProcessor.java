package org.gameserver.auth.processor;

import java.util.Set;

import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.util.redis.RedisCacheClient;
import com.jyg.util.TokenUtil;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

import javax.inject.Inject;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class LoginHttpProcessor extends HttpProcessor {

	private final RedisCacheClient redisCacheClient;

	@Inject
	public LoginHttpProcessor(RedisCacheClient redisCacheClient) {
		this.redisCacheClient = redisCacheClient;
	}

	@Override
	public void service(Request request, Response response) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println(username + " >> " + password);

		if (!checkLogin(username, password)) {
			getDispatcher("/404").service(request, response);
			return;
		}
		String token = TokenUtil.getToken();
		String setResult = null;
		try {
			setResult = redisCacheClient.setValueExpire(username, 60, token);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (setResult == null) {
			System.out.println(" set value fail ");
		} else {
			System.out.println(" set value success " + setResult);
		}
		request.decodeCookies();

		Set<Cookie> cookies = request.decodeCookies();
		for (Cookie c : cookies) {
			System.out.println(c.name() + " : " + c.value());
		}
		Cookie cookie = new DefaultCookie("jyg", "jia");
		cookie.setMaxAge(60 * 60L);
//		cookie.setDomain("127.0.0.1");
		cookie.setPath("/");
		response.addCookie(cookie);
		response.writeAndFlush("<html><head></head><body>welcome user " + request.getParameter("username") + " to index," + " token :" + token + "<body></html>");

	}


	private boolean checkLogin(String username, String password) {
		if (username == null || username.length() == 0) {
			return false;
		}
		if (password == null || password.length() == 0) {
			return false;
		}

		return true;
	}


}


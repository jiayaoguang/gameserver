package org.jyg.gameserver.auth.processor;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.core.util.TokenUtil;

import java.util.Set;

//import org.jyg.gameserver.core.util.redis.RedisCacheClient;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class LoginHttpProcessor extends HttpProcessor {

//	private final RedisCacheClient redisCacheClient;

	public LoginHttpProcessor() {
//		this.redisCacheClient = redisCacheClient;
		super("/index");
	}

	@Override
	public void service(Request request, Response response) {
		String account = request.getParameter("username");
		String password = request.getParameter("password");
		Logs.DEFAULT_LOGGER.info(account + " >> " + password);

		if (!checkLogin(account, password)) {
			response.sendRedirect("/login.html");
			return;
		}


		String token = TokenUtil.getToken();

		getContext().getSingleThreadExecutorManager(request.getRequestid()).execute(()->{

			String setResult = null;
			try {
//				setResult = redisCacheClient.setValueExpire(account, 60, token);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (setResult == null) {
				Logs.DEFAULT_LOGGER.info(" set value fail ");
			} else {
				Logs.DEFAULT_LOGGER.info(" set value success " + setResult);
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

		});


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


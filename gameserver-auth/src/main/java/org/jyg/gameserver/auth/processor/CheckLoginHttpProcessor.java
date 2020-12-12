package org.jyg.gameserver.auth.processor;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.util.TokenUtil;
//import org.jyg.gameserver.core.util.redis.RedisCacheClient;

import javax.inject.Inject;
import java.util.Set;

/**
 * create by jiayaoguang on 2020/7/11
 */
public class CheckLoginHttpProcessor extends HttpProcessor {

//	private final RedisCacheClient redisCacheClient;

	@Inject
	public CheckLoginHttpProcessor() {
//		this.redisCacheClient = redisCacheClient;
	}

	@Override
	public void service(Request request, Response response) {
		String account = request.getParameter("account");
		String myToken = request.getParameter("token");
		System.out.println(account + " >> " + myToken);

		if (!StringUtils.isEmpty(account) || StringUtils.isEmpty(myToken)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("account" , account);
			jsonObject.put("isTokenRight" , false);

			response.writeAndFlush(jsonObject.toJSONString());
			return;
		}


		getContext().getSingleThreadExecutorManager(request.getRequestid()).execute(()->{

			String token = null;
			try {
//				token = redisCacheClient.getValue(account);
			} catch (Exception e) {
				e.printStackTrace();
			}
			boolean isTokenRight = false;
			if (StringUtils.isEmpty(token)) {
				System.out.println(" token is null ");
			}else if(token.equals(myToken)){
				isTokenRight = true;
			}
			response.setContentType(Response.CONTENT_TYPE_JSON);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("account" , account);
			jsonObject.put("isTokenRight" , isTokenRight);

			response.writeAndFlush(jsonObject.toJSONString());

		});


	}



}


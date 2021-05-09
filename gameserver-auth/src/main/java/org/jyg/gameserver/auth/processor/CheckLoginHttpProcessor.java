package org.jyg.gameserver.auth.processor;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.util.Logs;
//import org.jyg.gameserver.core.util.redis.RedisCacheClient;


/**
 * create by jiayaoguang on 2020/7/11
 */
public class CheckLoginHttpProcessor extends HttpProcessor {

//	private final RedisCacheClient redisCacheClient;

	public CheckLoginHttpProcessor() {
//		this.redisCacheClient = redisCacheClient;
	}

	@Override
	public void service(Request request, Response response) {
		String account = request.getParameter("account");
		String myToken = request.getParameter("token");
		Logs.DEFAULT_LOGGER.info(account + " >> " + myToken);

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
				Logs.DEFAULT_LOGGER.info(" token is null ");
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


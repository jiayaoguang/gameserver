package org.jyg.gameserver.auth.processor;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.util.Logs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
//import org.jyg.gameserver.core.util.redis.RedisCacheClient;


/**
 * create by jiayaoguang on 2020/7/11
 */
public class CheckLoginHttpProcessor extends HttpProcessor {

//	private final RedisCacheClient redisCacheClient;

	private final JsonMapper jsonMapper = new JsonMapper();

	private Random random = new Random();


	public CheckLoginHttpProcessor() {
//		this.redisCacheClient = redisCacheClient;
		super("/checkToken");
	}

	@Override
	public void service(Request request, Response response) {
		String account = request.getParameter("account");
		String myToken = request.getParameter("token");
		Logs.DEFAULT_LOGGER.info(account + " >> " + myToken);

		if (!StringUtils.isEmpty(account) || StringUtils.isEmpty(myToken)) {
			Map<String,Object> returnParams = new HashMap<>();
			returnParams.put("account" , account);
			returnParams.put("isTokenRight" , false);

			try {
				response.writeAndFlush(jsonMapper.writeValueAsString(returnParams));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				response.write500Error();
			}
			return;
		}
		int chooseExecutorId = random.nextInt();


		getContext().getSingleThreadExecutorManager(chooseExecutorId).execute(()->{

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

			Map<String,Object> params = new HashMap<>();
			params.put("account" , account);
			params.put("isTokenRight" , isTokenRight);

			try {
				response.writeAndFlush(jsonMapper.writeValueAsString(params));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				response.write500Error();
			}

		});


	}



}


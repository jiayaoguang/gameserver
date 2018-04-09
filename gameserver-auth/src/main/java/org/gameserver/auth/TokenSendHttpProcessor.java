package org.gameserver.auth;

import java.util.concurrent.atomic.AtomicLong;

import com.jyg.bean.LogicEvent;
import com.jyg.net.EventDispatcher;
import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.proto.p_auth_sm.p_auth_sm_request_send_token;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class TokenSendHttpProcessor extends HttpProcessor{
	TokenMrg token = new TokenMrg();
	Channel channel;
	Long2ObjectMap<Channel> requestidTohttpChannelMap = EventDispatcher.getInstance().getRequestidTohttpChannelMap();
	
	public TokenSendHttpProcessor(Channel channel){
		this.channel = channel;
	}
	

	AtomicLong requestId = new AtomicLong(1);
	
	@Override
	public void process(LogicEvent<Request> event) {
		
		p_auth_sm_request_send_token.Builder builder = p_auth_sm_request_send_token.newBuilder();
		long id = requestId.getAndIncrement();
		builder.setRequestId(id);
		
		requestidTohttpChannelMap.put(id, event.getChannel());
		
		builder.setToken(TokenMrg.getToken());
		
		channel.writeAndFlush(builder);
		
	}
	
	//TODO 修改为回调
	@Override
	public void service(Request request, Response response) {
		
		response.write("<html><head></head><body>welcome user "+ request.getParameter("username") +" to index,"+ " token :" +token.getToken()+"<body></html>");
	}

}


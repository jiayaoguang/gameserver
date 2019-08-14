package org.gameserver.auth.processor;

import org.gameserver.auth.bean.UserLoginInfo;
import org.gameserver.auth.util.AuthToSMChannelMrg;

import com.google.inject.Inject;
import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;
import com.jyg.proto.p_auth_sm.p_auth_sm_request_send_token;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class TokenSendHttpProcessor extends HttpProcessor{
	private AuthToSMChannelMrg authToSMChannel;
	private Long2ObjectMap<UserLoginInfo> requestidTohttpChannelMap = new Long2ObjectOpenHashMap<>();
	
	public UserLoginInfo getUserLoginInfo(long requestId) {
		return requestidTohttpChannelMap.get(requestId);
	}
	
	@Inject
	public TokenSendHttpProcessor(AuthToSMChannelMrg authToSMChannel){
		this.authToSMChannel = authToSMChannel;
	}
	

//	AtomicLong requestId = new AtomicLong(1);
	
//	@Override
//	public void process(LogicEvent<Request> event) {
//		
//		p_auth_sm_request_send_token.Builder builder = p_auth_sm_request_send_token.newBuilder();
//		long id = requestId.getAndIncrement();
//		builder.setRequestId(id);
//		
//		requestidTohttpChannelMap.put(id, event.getChannel());
//		
//		Channel smChannel = authToSmChannelMrg.checkoutAndGetChannel();
//		if(smChannel != null) {
//			smChannel.writeAndFlush(builder);
//		}else {
//			Response response = new Response();
//			DefaultFullHttpResponse fullHttpResponse = null;
//			fullHttpResponse = response.create500FullHttpResponse();
//
//			event.getChannel().writeAndFlush(fullHttpResponse);
//		}
//		
//		
//		System.out.println(".................");
//		
//	}
	
	
	
	@Override
	public void service(Request request, Response response) {
		p_auth_sm_request_send_token.Builder builder = p_auth_sm_request_send_token.newBuilder();
//		long id = requestId.getAndIncrement();
		builder.setRequestId(request.getRequestid());
		String username = request.getParameter("username");
		
		if(username == null) {
			return;
		}
		
		requestidTohttpChannelMap.put(request.getRequestid(), new UserLoginInfo(  response.getChannel() ,username ) );
		
		Channel channel  = authToSMChannel.checkoutAndGetChannel();
		if(channel == null){
			return;
		}
		channel.writeAndFlush(builder);
		
//		authToSMChannelPool.returnChannel(tuple);
		
	}

}


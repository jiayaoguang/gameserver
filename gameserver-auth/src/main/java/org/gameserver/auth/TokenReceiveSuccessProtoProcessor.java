package org.gameserver.auth;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.net.EventDispatcher;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.net.Response;
import com.jyg.proto.p_auth_sm.p_sm_auth_response_receive_token;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

/**
 * created by jiayaoguang at 2018年4月9日
 */
public class TokenReceiveSuccessProtoProcessor extends ProtoProcessor<p_sm_auth_response_receive_token> {

	Long2ObjectMap<Channel> requestidTohttpChannelMap = EventDispatcher.getInstance().getRequestidTohttpChannelMap();
	
	public TokenReceiveSuccessProtoProcessor() throws InstantiationException, IllegalAccessException {
		super(p_sm_auth_response_receive_token.newBuilder().build());
	}

	@Override
	public void processProtoMessage(p_sm_auth_response_receive_token msg, ProtoResponse response) {
		long requestId = msg.getRequestId();
		Channel channel = requestidTohttpChannelMap.get(requestId);
		Response httpResponse = new Response();
		httpResponse.write("<html><head></head><body>welcome user  to index,"+ " token <body></html>");
		channel.writeAndFlush(httpResponse.createDefaultFullHttpResponse());
	}

}


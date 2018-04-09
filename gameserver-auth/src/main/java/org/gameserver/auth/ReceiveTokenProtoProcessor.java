package org.gameserver.auth;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.bean.LogicEvent;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.proto.p_auth_sm;
import com.jyg.proto.p_auth_sm.p_sm_auth_response_receive_token;

import io.netty.channel.Channel;

/**
 * created by jiayaoguang at 2018年4月8日
 */
public class ReceiveTokenProtoProcessor extends ProtoProcessor<p_sm_auth_response_receive_token> {

	private Channel channel;

	public ReceiveTokenProtoProcessor(Class<GeneratedMessageV3> protoClazz,Channel channel)
			throws InstantiationException, IllegalAccessException {
		super(protoClazz);
		this.channel = channel;
	}

	@Override
	public void process(LogicEvent<p_sm_auth_response_receive_token> event) {
		
	}
	
	@Override
	public void processProtoMessage(p_sm_auth_response_receive_token msg, ProtoResponse response) {
		
		
		
	}

}


package com.jyg.process;

import java.lang.reflect.InvocationTargetException;

import com.jyg.net.EventDispatcher;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.proto.p_common.p_common_response_pong;
import com.jyg.session.Session;

/**
 * created by jiayaoguang at 2018年4月16日
 */
public class PingProtoProcessor extends ProtoProcessor<p_common_response_pong>{

	
	
	public PingProtoProcessor()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		super(p_common_response_pong.newBuilder().build());
	}

	@Override
	public void processProtoMessage(p_common_response_pong msg, ProtoResponse response) {
		Session session = EventDispatcher.getInstance().getSession(response.getChannel());
		
		if(session == null) {
			return;
		}
		
		session.setLastContactMill(System.currentTimeMillis());
		
	}

}


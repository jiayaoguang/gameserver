package com.jyg.process;

import java.lang.reflect.InvocationTargetException;

import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.proto.p_common.p_common_response_pong;
import com.jyg.session.ServerSession;
import com.jyg.session.Session;

/**
 * created by jiayaoguang at 2018年4月16日
 */
public class PongProtoProcessor extends ProtoProcessor<p_common_response_pong>{

	Session session ;
	
	public PongProtoProcessor()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		super(p_common_response_pong.newBuilder().build());
		session = new ServerSession();
	}

	@Override
	public void processProtoMessage(p_common_response_pong msg, ProtoResponse response) {
		
		session.setLastContactMill(System.currentTimeMillis());
		
	}

}


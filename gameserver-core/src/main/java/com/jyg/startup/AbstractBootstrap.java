package com.jyg.startup;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.net.EventDispatcher;
import com.jyg.processor.ProtoProcessor;

/**
 * create by jiayaoguang at 2018年3月5日
 * 远程端口连接
 */

public abstract class AbstractBootstrap {

	
	public AbstractBootstrap()  {
		
	}
	
	public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoprocessor) throws Exception {
		EventDispatcher.getInstance().registerSocketEvent(eventid, protoprocessor);
	}
	
	public void registerSendEventIdByProto(int eventId,Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
		EventDispatcher.getInstance().registerSendEventIdByProto( eventId, protoClazz);
	}


}

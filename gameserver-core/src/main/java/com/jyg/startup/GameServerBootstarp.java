package com.jyg.startup;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.net.EventDispatcher;
import com.jyg.net.HttpProcessor;
import com.jyg.net.Service;
import com.jyg.net.TcpService;
import com.jyg.util.GlobalQueue;

/**
 * Created by jiayaoguang on 2017/3/30
 */
public class GameServerBootstarp extends AbstractBootstrap {
	
	List<Service> services = new ArrayList<>(1);
	public GameServerBootstarp(){
		
		
	}
	
	public void addService(Service service) {
		services.add(service); 
	}
	
	
//	public void registerLogicEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> processor) throws Exception {
//		EventDispatcher.getInstance().registerLogicEvent(eventid, processor);
//	}
//	
//	public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoprocessor) throws Exception {
//		EventDispatcher.getInstance().registerSocketEvent(eventid, protoprocessor);
//	}
	
	public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
		EventDispatcher.getInstance().registerHttpEvent(path, processor);
	}
	
	public void registerSendEventIdByProto(int eventId,Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
		EventDispatcher.getInstance().registerSendEventIdByProto( eventId, protoClazz);
	}

    public void start() throws InterruptedException {
    	GlobalQueue.start();
    	if(services.size()==0) {
    		throw new IllegalArgumentException("services list is empty");
    	}
    	
    	for(Service service:services) {
    		service.start();
    	}
    }

}
package com.jyg.startup;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.net.EventDispatcher;
import com.jyg.net.HttpProcessor;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.Service;
import com.jyg.util.GlobalQueue;

/**
 * Created by jiayaoguang on 2017/3/30
 */
public class Bootstarp {
	
	List<Service> services = new ArrayList<>(1);
	
	public void addService(Service service) {
		services.add(service); 
	}
	
	public void registerLogicEvent(int eventid, ProtoProcessor processor) throws Exception {
		EventDispatcher.getInstance().registerLogicEvent(eventid, processor);
	}
	
	public void registerRpcEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoprocessor) throws Exception {
		EventDispatcher.getInstance().registerRpcEvent(eventid, protoprocessor);
	}
	
	public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
		EventDispatcher.getInstance().registerHttpEvent(path, processor);
	}

    public void start() throws Exception {
    	GlobalQueue.class.newInstance();
    	if(services.size()==0) {
    		throw new Exception("no port to listen");
    	}
    	for(Service service:services) {
    		service.start();
    	}
    }

}
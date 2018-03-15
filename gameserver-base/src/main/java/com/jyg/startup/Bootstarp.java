package com.jyg.startup;

import java.util.ArrayList;
import java.util.List;

import com.jyg.net.EventDispatcher;
import com.jyg.net.Processor;
import com.jyg.net.Service;
import com.jyg.net.WebSocketService;
import com.jyg.util.GlobalQueue;

/**
 * Created by jiayaoguang on 2017/3/30
 */
public class Bootstarp {
	
	List<Service> services = new ArrayList<>(1);
	
	public void addService(Service service) {
		services.add(service); 
	}
	
	public void register(short eventid, Processor processor) throws Exception {
		EventDispatcher.getInstance().registerLogicEvent(eventid, processor);
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
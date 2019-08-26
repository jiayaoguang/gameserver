package com.jyg.startup;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.consumer.DefaultEventConsumerFactory;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.net.*;
import com.jyg.processor.HttpProcessor;
import com.jyg.util.GlobalQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiayaoguang on 2017/3/30
 */
public class GameServerBootstarp extends AbstractBootstrap {

	private EventConsumerFactory eventConsumerFactory;

	private final List<Service> services = new ArrayList<>(1);

	public GameServerBootstarp() {


	}

	public void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory) {
		if (isStart) {
			logger.error("oprete fail,server is already start ");
			return;
		}
		this.eventConsumerFactory = eventConsumerFactory;
	}


	public void addService(Service service) {
		if (isStart) {
			logger.error("oprete fail,server is already start ");
			return;
		}
		services.add(service);
	}

	public void addTcpService(int port) {
		if (isStart) {
			logger.error("oprete fail,server is already start ");
			return;
		}
		services.add(new SocketService(port));
	}

	public void addWebSocketService(int port) {
		if (isStart) {
			logger.error("oprete fail,server is already start ");
			return;
		}
		services.add(new WebSocketService(port));
	}

	public void addHttpService(int port) {
		if (isStart) {
			logger.error("oprete fail,server is already start ");
			return;
		}
		services.add(new HttpService(port));
	}


	public void start() throws InterruptedException {

		if (isStart) {
			logger.error("server is already start ");
			return;
		}
		if (eventConsumerFactory == null) {
			eventConsumerFactory = new DefaultEventConsumerFactory();
		}

		if (services.isEmpty()) {
			throw new IllegalArgumentException("services list is empty");
		}
		GlobalQueue.start(eventConsumerFactory);

		for (Service service : services) {
			service.start();
		}
		isStart = true;
	}

}
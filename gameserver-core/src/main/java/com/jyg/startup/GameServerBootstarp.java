package com.jyg.startup;

import com.jyg.consumer.DefaultEventConsumerFactory;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.net.HttpService;
import com.jyg.net.Service;
import com.jyg.net.SocketService;
import com.jyg.net.WebSocketService;
import com.jyg.util.IGlobalQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiayaoguang on 2017/3/30
 */
public class GameServerBootstarp extends AbstractBootstrap {

    private EventConsumerFactory eventConsumerFactory;

    private final List<Service> services = new ArrayList<>(1);

    public GameServerBootstarp() {
        super();
    }

    public GameServerBootstarp(IGlobalQueue globalQueue) {
        super(globalQueue);
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
        services.add(new SocketService(port , globalQueue));
    }

    public void addWebSocketService(int port) {
        if (isStart) {
            logger.error("oprete fail,server is already start ");
            return;
        }
        services.add(new WebSocketService(port ,globalQueue ));
    }

    public void addHttpService(int port) {
        if (isStart) {
            logger.error("oprete fail,server is already start ");
            return;
        }
        services.add(new HttpService(port , globalQueue));
    }


    @Override
    public synchronized void start() throws InterruptedException {

        if (isStart) {
            logger.error("server is already start ");
            return;
        }

        isStart = true;

        if (eventConsumerFactory == null) {
            eventConsumerFactory = new DefaultEventConsumerFactory();
        }

        if (services.isEmpty()) {
            throw new IllegalArgumentException("services list is empty");
        }
        globalQueue.start();

        for (Service service : services) {
            service.start();
        }
    }


    public IGlobalQueue getGlobalQueue() {
        return globalQueue;
    }
}
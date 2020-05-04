package com.jyg.startup;

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


    private final List<Service> services = new ArrayList<>(1);

    public GameServerBootstarp() {
        super();
    }

    public GameServerBootstarp(IGlobalQueue globalQueue) {
        super(globalQueue);
    }


    public void addService(Service service) {
        if (isStart) {
            logger.error("oprete fail,server is already start ");
            return;
        }
        services.add(service);
    }

    public void addTcpService(int port) {
        addService(new SocketService(port , getContext()));
    }

    public void addWebSocketService(int port) {
        addService(new WebSocketService(port ,getContext() ));
    }

    public void addHttpService(int port) {
        addService(new HttpService(port , getContext()));
    }


    @Override
    public void doStart() throws InterruptedException {

        if (services.isEmpty()) {
            throw new IllegalArgumentException("services list is empty");
        }
        globalQueue.start();

        for (Service service : services) {
            service.start();
        }
    }

    @Override
    public void stop(){
        getContext().getEventLoopGroupManager().stopAllEventLoop();

        globalQueue.stop();

        for (Service service : services) {
            service.stop();
        }
    }


    public IGlobalQueue getGlobalQueue() {
        return globalQueue;
    }
}
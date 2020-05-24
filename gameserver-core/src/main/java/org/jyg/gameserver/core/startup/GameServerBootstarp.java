package org.jyg.gameserver.core.startup;

import org.jyg.gameserver.core.net.HttpService;
import org.jyg.gameserver.core.net.Service;
import org.jyg.gameserver.core.net.SocketService;
import org.jyg.gameserver.core.net.WebSocketService;
import org.jyg.gameserver.core.consumer.IGlobalQueue;

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
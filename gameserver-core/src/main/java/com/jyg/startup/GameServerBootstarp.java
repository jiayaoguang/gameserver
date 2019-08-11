package com.jyg.startup;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.net.*;
import com.jyg.util.GlobalQueue;

/**
 * Created by jiayaoguang on 2017/3/30
 */
public class GameServerBootstarp extends AbstractBootstrap {

    private final List<Service> services = new ArrayList<>(1);

    public GameServerBootstarp() {


    }

    public void addService(Service service) {
        services.add(service);
    }

    public void addTcpService(int port) {
        services.add(new SocketService(port));
    }

    public void addWebSocketService(int port) {
        services.add(new WebSocketService(port));
    }

    public void addHttpService(int port) {
        services.add(new HttpService(port));
    }


    public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
        EventDispatcher.getInstance().registerHttpEvent(path, processor);
    }

    public void registerSendEventIdByProto(int eventId, Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
        EventDispatcher.getInstance().registerSendEventIdByProto(eventId, protoClazz);
    }

    public void start() throws InterruptedException {
        GlobalQueue.start();
        if (services.isEmpty()) {
            throw new IllegalArgumentException("services list is empty");
        }

        for (Service service : services) {
            service.start();
        }
    }

}
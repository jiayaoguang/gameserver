package org.jyg.gameserver.core.startup;

import org.jyg.gameserver.core.net.*;
import org.jyg.gameserver.core.net.Connector;
import org.jyg.gameserver.core.net.SocketConnector;
import org.jyg.gameserver.core.consumer.Consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiayaoguang on 2017/3/30
 */
public class GameServerBootstrap extends AbstractBootstrap {


    private final List<Connector> connectors = new ArrayList<>(1);

    public GameServerBootstrap() {
        super();
    }

    public GameServerBootstrap(Consumer defaultConsumer) {
        super(defaultConsumer);
    }

    /**
     * (see {@link #addConnector(Connector)}
     *
     * @param connector connector
     */
    @Deprecated
    public void addService(Connector connector) {
        addConnector(connector);
    }


    public void addConnector(Connector connector) {
        if (isStart) {
            logger.error("oprete fail,server is already start ");
            return;
        }
        connectors.add(connector);
    }

    /**
     * (see {@link #addTcpConnector(int)}
     *
     * @param port port
     */
    @Deprecated
    public void addTcpService(int port) {
        addConnector(new SocketConnector(port, getContext()));
    }

    public void addTcpConnector(int port) {
        addConnector(new SocketConnector(port, getContext()));
    }

    /**
     * (see {@link #addWebSocketConnector(int)}
     *
     * @param port port
     */
    @Deprecated
    public void addWebSocketService(int port) {
        addConnector(new WebSocketConnector(port, getContext()));
    }

    public void addWebSocketConnector(int port) {
        addConnector(new WebSocketConnector(port, getContext()));
    }

    /**
     * (see {@link #addHttpConnector(int)}
     *
     * @param port port
     */
    @Deprecated
    public void addHttpService(int port) {
        addConnector(new HttpConnector(port, getContext()));
    }

    public void addHttpConnector(int port) {
        addConnector(new HttpConnector(port, getContext()));
    }


    @Override
    public void doStart() throws InterruptedException {

        if (connectors.isEmpty()) {
            throw new IllegalArgumentException("services list is empty");
        }

        for (Connector connector : connectors) {
            connector.start();
        }
    }

    @Override
    public void stop() {
        getContext().getEventLoopGroupManager().stopAllEventLoop();


        for (Connector connector : connectors) {
            connector.stop();
        }
    }


}
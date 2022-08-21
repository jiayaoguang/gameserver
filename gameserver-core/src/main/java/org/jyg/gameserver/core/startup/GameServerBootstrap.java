package org.jyg.gameserver.core.startup;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.net.*;
import org.jyg.gameserver.core.net.Connector;
import org.jyg.gameserver.core.net.SocketConnector;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

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

    public GameServerBootstrap(GameConsumer defaultGameConsumer) {
        super(defaultGameConsumer);
    }


    public GameServerBootstrap(GameContext gameContext) {
        super(gameContext);
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
        addConnector(new SocketConnector(port, getGameContext()));
    }

    public void addTcpConnector(int port) {
        addConnector(new SocketConnector(port, getGameContext()));
    }

    /**
     * (see {@link #addWebSocketConnector(int)}
     *
     * @param port port
     */
    @Deprecated
    public void addWebSocketService(int port) {
        addConnector(new WebSocketConnector(port, getGameContext()));
    }

    public void addWebSocketConnector(int port) {
        addConnector(new WebSocketConnector(port, getGameContext()));
    }

    /**
     * (see {@link #addHttpConnector(int)}
     *
     * @param port port
     */
    @Deprecated
    public void addHttpService(int port) {
        addConnector(new HttpConnector(port, getGameContext()));
    }

    public void addHttpConnector(int port) {
        addConnector(new HttpConnector(port, getGameContext()));
    }


    @Override
    public void doStart(){

        if (connectors.isEmpty()) {
//            throw new IllegalArgumentException("connectors list is empty");
            Logs.DEFAULT_LOGGER.info("connectors list is empty");
        }

        for (Connector connector : connectors) {
            connector.start();
        }
    }

//    @Override
//    public void stop() {
//        getContext().getEventLoopGroupManager().stop();
//
//
//        for (Connector connector : connectors) {
//            connector.stop();
//        }
//    }


}
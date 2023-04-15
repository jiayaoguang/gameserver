package org.jyg.gameserver.route;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ConnectEvent;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.DisconnectEvent;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.msg.route.RouteClientSessionConnectMsg;
import org.jyg.gameserver.core.msg.route.RouteClientSessionDisconnectMsg;
import org.jyg.gameserver.core.msg.route.RouteRegisterMsg;
import org.jyg.gameserver.core.session.EnumSessionType;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.route.processor.RouteMsgToGameMsgHandler;
import org.jyg.gameserver.route.processor.RouteMsgToGameMsgProcessor;
import org.jyg.gameserver.route.processor.RouteReplyMsgProcessor;


/**
 * Hello world!
 */
public class GameRouteBootstarp extends GameServerBootstrap {



    public static final int REMOTE_CONSUMER_ID = 1009;


    private RouteConfig routeConfig;



    public GameRouteBootstarp() {
        super();
        tryInitRouteConfig();
    }

    public GameRouteBootstarp(GameConsumer defaultGameConsumer) {
        super(defaultGameConsumer);
        tryInitRouteConfig();
    }


    public GameRouteBootstarp(GameContext gameContext) {
        super(gameContext);
        tryInitRouteConfig();
    }


    private void tryInitRouteConfig(){

        RouteConfig routeConfig = ConfigUtil.properties2Object(GameContext.DEFAULT_CONFIG_FILE_NAME, RouteConfig.class);
        if (routeConfig == null) {
            throw new IllegalArgumentException("routeConfig error");
        }
        setRouteConfig(routeConfig);
    }


    private void setRouteConfig(RouteConfig routeConfig) {
        if(getGameContext().isStart()){
            throw new IllegalStateException("server is start set RouteConfig fail");
        }
        this.routeConfig = routeConfig;
    }

    @Override
    public void beforeStart() {

        super.beforeStart();


        GameServerBootstrap bootstarp = this;
//	    RedisCacheClient redisCacheClient = injector.getInstance(RedisCacheClient.class);
//	    redisCacheClient.init();

//        bootstarp.registerHttpEvent("/index000", injector.getInstance(TokenSendHttpProcessor.class));


//        bootstarp.addProtoProcessor(1000, injector.getInstance(LoginProtoProcessor.class));


//        this.addHttpConnector(8082);
//
//        this.addTcpConnector(8081);


//        RemoteConsumerInfo remoteConsumerInfo = new RemoteConsumerInfo();
//        remoteConsumerInfo.setIp(routeConfig.getRemoteGameServerIp() );
//        remoteConsumerInfo.setPort(routeConfig.getRemoteGameServerPort());
//        int remoteConsumerId = REMOTE_CONSUMER_ID;
//        remoteConsumerInfo.setConsumerId(remoteConsumerId);
//
//        RemoteGameConsumer remoteConsumer = new RemoteGameConsumer(bootstarp.getGameContext(), remoteConsumerInfo);
//        remoteConsumer.setId(remoteConsumerId);

//        bootstarp.getGameContext().getConsumerManager().addConsumer(remoteConsumer);
        bootstarp.getDefaultConsumer().putInstance(new RemoteServerManager(bootstarp.getGameContext(), routeConfig.getRemoteGameServerIp(), routeConfig.getRemoteGameServerPort()));


        bootstarp.addByteMsgObjProcessor(new RouteReplyMsgProcessor());

        final GameContext gameContext = bootstarp.getGameContext();


        bootstarp.getGameContext().getMainGameConsumer().setUnknownMsgHandler(new RouteMsgToGameMsgHandler(gameContext));
        bootstarp.getGameContext().getMainGameConsumer().setUnknownProcessor(new RouteMsgToGameMsgProcessor());




        bootstarp.getDefaultConsumer().getEventManager().addEventListener(new GameEventListener<ConnectEvent>() {
            @Override
            public void onEvent(ConnectEvent event) {
                Session session = event.getSession();
                if (session.getSessionType() != EnumSessionType.NORMAL_CLIENT.type) {
                    return;
                }

                RouteClientSessionConnectMsg routeClientSessionConnectMsg = new RouteClientSessionConnectMsg();
                routeClientSessionConnectMsg.setSessionId(session.getSessionId());
                routeClientSessionConnectMsg.setAddr(session.getRemoteAddr());

                event.getGameConsumer().getInstance(RemoteServerManager.class).sendRemoteMsg(routeClientSessionConnectMsg);
            }
        });



        bootstarp.getDefaultConsumer().getEventManager().addEventListener(new GameEventListener<DisconnectEvent>() {


            @Override
            public void onEvent(DisconnectEvent event) {
                RouteClientSessionDisconnectMsg routeClientSessionDisconnectMsg = new RouteClientSessionDisconnectMsg();
                routeClientSessionDisconnectMsg.setSessionId(event.getSession().getSessionId());
                event.getGameConsumer().getInstance(RemoteServerManager.class).sendRemoteMsg(routeClientSessionDisconnectMsg);
            }
        });


        bootstarp.getDefaultConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>() {
            @Override
            public void onEvent(ConsumerThreadStartEvent event) {

                GameConsumer con = event.getGameConsumer();

                RouteRegisterMsg routeRegisterMsg = new RouteRegisterMsg();
                routeRegisterMsg.setServerId(con.getGameContext().getServerConfig().getServerId());

                event.getGameConsumer().getInstance(RemoteServerManager.class).sendRemoteMsg(routeRegisterMsg);
            }
        });


//        bootstarp.registerSocketEvent(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(),
//        		new TokenReceiveSuccessProtoProcessor());
    }


    public RouteConfig getRouteConfig() {
        return routeConfig;
    }

}

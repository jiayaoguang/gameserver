package org.jyg.gameserver.route;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.RemoteGameConsumer;
import org.jyg.gameserver.core.event.*;
import org.jyg.gameserver.core.msg.route.*;
import org.jyg.gameserver.core.session.EnumSessionType;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.route.processor.RouteMsgToGameMsgHandler;
import org.jyg.gameserver.route.processor.RouteMsgToGameMsgProcessor;
import org.jyg.gameserver.route.processor.RouteReplyMsgProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Hello world!
 *
 */
public class RouteBootstarp
{

	private static final Logger logger = LoggerFactory.getLogger(RouteBootstarp.class);

    public static void main ( String[] args ) throws Exception 
    {
    	

    	GameServerBootstrap bootstarp = new GameServerBootstrap();
//	    RedisCacheClient redisCacheClient = injector.getInstance(RedisCacheClient.class);
//	    redisCacheClient.init();

//        bootstarp.registerHttpEvent("/index000", injector.getInstance(TokenSendHttpProcessor.class));
        

//        bootstarp.addProtoProcessor(1000, injector.getInstance(LoginProtoProcessor.class));


        bootstarp.addHttpConnector(8082);

        bootstarp.addTcpConnector(8081);


        RouteConfig routeConfig = ConfigUtil.properties2Object("jyg",RouteConfig.class );
        if(routeConfig == null){
            throw new IllegalArgumentException("routeConfig error");
        }

        RemoteGameConsumer remoteConsumer = new RemoteGameConsumer(bootstarp.getGameContext(),routeConfig.getGameServerIp(),routeConfig.getGameServerPort());
        int remoteConsumerId = 1009;
        remoteConsumer.setId(remoteConsumerId);

        bootstarp.getGameContext().getConsumerManager().addConsumer(remoteConsumer);
        bootstarp.getGameContext().putInstance(new RemoteServerManager(bootstarp.getGameContext() , remoteConsumerId));



        bootstarp.addByteMsgObjProcessor(new RouteReplyMsgProcessor());

        final GameContext gameContext = bootstarp.getGameContext();


        bootstarp.getGameContext().getDefaultGameConsumer().setUnknownMsgHandler(new RouteMsgToGameMsgHandler(gameContext));
        bootstarp.getGameContext().getDefaultGameConsumer().setUnknownProcessor(new RouteMsgToGameMsgProcessor());




        bootstarp.getDefaultConsumer().getEventManager().addEventListener((GameEventListener<ConnectEvent>) event -> {

            Session session = event.getSession();
            if(session.getSessionType() != EnumSessionType.NORMAL_CLIENT.type){
                return;
            }

            RouteClientSessionConnectMsg routeClientSessionConnectMsg = new RouteClientSessionConnectMsg();
            routeClientSessionConnectMsg.setSessionId(session.getSessionId());
            routeClientSessionConnectMsg.setAddr(session.getRemoteAddr());

            bootstarp.getGameContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeClientSessionConnectMsg);
        });


        bootstarp.getDefaultConsumer().getEventManager().addEventListener(new GameEventListener<DisconnectEvent>() {
            @Override
            public void onEvent(DisconnectEvent event) {
                RouteClientSessionDisconnectMsg routeClientSessionDisconnectMsg = new RouteClientSessionDisconnectMsg();
                routeClientSessionDisconnectMsg.setSessionId(event.getSession().getSessionId());
                bootstarp.getGameContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeClientSessionDisconnectMsg);
            }
        });



        bootstarp.getDefaultConsumer().getEventManager().addEventListener(new GameEventListener<ConsumerThreadStartEvent>() {
            @Override
            public void onEvent(ConsumerThreadStartEvent event) {

                GameConsumer con = event.getGameConsumer();

                RouteRegisterMsg routeRegisterMsg = new RouteRegisterMsg();
                routeRegisterMsg.setServerId(con.getGameContext().getServerConfig().getServerId());

                con.getGameContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeRegisterMsg);
            }
        });

        
//        bootstarp.registerSocketEvent(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(),
//        		new TokenReceiveSuccessProtoProcessor());
        
        bootstarp.start();
        logger.info(" start success ");
    }
}

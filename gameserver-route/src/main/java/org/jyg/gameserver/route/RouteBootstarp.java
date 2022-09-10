package org.jyg.gameserver.route;

import org.jyg.gameserver.core.consumer.RemoteGameConsumer;
import org.jyg.gameserver.core.event.ConnectEvent;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.DisconnectEvent;
import org.jyg.gameserver.core.msg.route.*;
import org.jyg.gameserver.core.session.EnumSessionType;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.UnknownMsgHandler;
import org.jyg.gameserver.route.msg.ChatMsgObj;
import org.jyg.gameserver.route.msg.ChatReplyMsgObj;
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




        bootstarp.getDefaultConsumer().getEventManager().addEvent(new ConnectEvent((session,obj)->{

            if(session.getSessionType() != EnumSessionType.NORMAL_CLIENT.type){
                return;
            }

            RouteClientSessionConnectMsg routeClientSessionConnectMsg = new RouteClientSessionConnectMsg();
            routeClientSessionConnectMsg.setSessionId(session.getSessionId());
            routeClientSessionConnectMsg.setAddr(session.getRemoteAddr());

            bootstarp.getGameContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeClientSessionConnectMsg);
        }));


        bootstarp.getDefaultConsumer().getEventManager().addEvent(new DisconnectEvent((session, obj)->{

            RouteClientSessionDisconnectMsg routeClientSessionDisconnectMsg = new RouteClientSessionDisconnectMsg();
            routeClientSessionDisconnectMsg.setSessionId(session.getSessionId());
            bootstarp.getGameContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeClientSessionDisconnectMsg);
        }));



        bootstarp.getDefaultConsumer().getEventManager().addEvent(new ConsumerThreadStartEvent((con,obj)->{

            RouteRegisterMsg routeRegisterMsg = new RouteRegisterMsg();
            routeRegisterMsg.setServerId(con.getGameContext().getServerConfig().getServerId());

            con.getGameContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeRegisterMsg);
        }));

        
//        bootstarp.registerSocketEvent(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(),
//        		new TokenReceiveSuccessProtoProcessor());
        
        bootstarp.start();
        logger.info(" start success ");
    }
}

package org.jyg.gameserver.route;

import org.jyg.gameserver.core.constant.MsgIdConst;
import org.jyg.gameserver.core.consumer.RemoteConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.route.RouteMsg;
import org.jyg.gameserver.core.msg.route.RouteRegisterMsg;
import org.jyg.gameserver.core.msg.route.RouteRegisterReplyMsg;
import org.jyg.gameserver.core.msg.route.RouteReplyMsg;
import org.jyg.gameserver.core.processor.AbstractProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.ConfigUtil;
import org.jyg.gameserver.route.msg.ChatMsgObj;
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

        RemoteConsumer remoteConsumer = new RemoteConsumer(bootstarp.getContext(),routeConfig.getGameServerIp(),routeConfig.getGameServerPort());
        int remoteConsumerId = 1009;
        remoteConsumer.setId(remoteConsumerId);

        bootstarp.getContext().getConsumerManager().addConsumer(remoteConsumer);
        bootstarp.getContext().putInstance(new RemoteServerManager(bootstarp.getContext() , remoteConsumerId));


        bootstarp.getContext().addMsgId2MsgClassMapping(MsgIdConst.ROUTE_MSG_ID , RouteMsg.class);
        bootstarp.getContext().addMsgId2MsgClassMapping(MsgIdConst.ROUTE_REPLY_MSG_ID , RouteReplyMsg.class);
        bootstarp.getContext().addMsgId2MsgClassMapping(MsgIdConst.ROUTE_REGISTER_MSG_ID , RouteRegisterMsg.class);
        bootstarp.getContext().addMsgId2MsgClassMapping(MsgIdConst.ROUTE_REGISTER_REPLY_MSG_ID , RouteRegisterReplyMsg.class);
        bootstarp.getContext().addMsgId2MsgClassMapping(1001, ChatMsgObj.class);

        bootstarp.addByteMsgObjProcessor(new RouteReplyMsgProcessor());

        bootstarp.getDefaultConsumer().setDefaultProcessor(new AbstractProcessor() {
            @Override
            public void process(Session session, EventData event) {
                RouteMsg routeMsg = new RouteMsg();
                routeMsg.setMsgId(event.getEventId());
                AbstractMsgCodec msgCodec = getContext().getMsgCodec(event.getEventId());

                try {
                    byte[] msgData = msgCodec.encode(event.getData());
                    routeMsg.setData(msgData);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                getContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeMsg);
            }
        });


        bootstarp.getDefaultConsumer().getEventManager().addEvent(new ConsumerThreadStartEvent((con,obj)->{

            RouteRegisterMsg routeRegisterMsg = new RouteRegisterMsg();
            routeRegisterMsg.setServerId(con.getContext().getServerConfig().getServerId());

            con.getContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeRegisterMsg);
        }));

        
//        bootstarp.registerSocketEvent(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(),
//        		new TokenReceiveSuccessProtoProcessor());
        
        bootstarp.start();
        logger.info(" start success ");
    }
}

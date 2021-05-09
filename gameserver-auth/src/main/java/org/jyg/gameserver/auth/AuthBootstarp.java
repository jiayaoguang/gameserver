package org.jyg.gameserver.auth;

import org.jyg.gameserver.auth.processor.*;
import org.jyg.gameserver.core.enums.ProtoEnum;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
//import org.jyg.gameserver.core.util.redis.RedisCacheClient;
import org.jyg.gameserver.proto.p_auth_sm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Hello world!
 *
 */
public class AuthBootstarp
{

	private static final Logger logger = LoggerFactory.getLogger(AuthBootstarp.class);

    public static void main ( String[] args ) throws Exception 
    {
    	

    	GameServerBootstrap bootstarp = new GameServerBootstrap();
//	    RedisCacheClient redisCacheClient = injector.getInstance(RedisCacheClient.class);
//	    redisCacheClient.init();

//        bootstarp.registerHttpEvent("/index000", injector.getInstance(TokenSendHttpProcessor.class));
        
        bootstarp.registerHttpEvent("/index", new LoginHttpProcessor());

        bootstarp.registerHttpEvent("/checkToken", new CheckLoginHttpProcessor());

        bootstarp.registerSendEventIdByProto(ProtoEnum.P_AUTH_SM_REQUEST_SEND_TOKEN.getEventId(),
        		p_auth_sm.p_auth_sm_request_send_token.class);

//        bootstarp.addMsgId2ProtoMapping(1001 , MsgLoginReply.class);
//        bootstarp.addMsgId2ProtoMapping(1000 , MsgLoginRequest.class);

//        bootstarp.addProtoProcessor(1000, injector.getInstance(LoginProtoProcessor.class));


        bootstarp.addHttpConnector(8088);

//        bootstarp.addTcpService(8081);
        
//        bootstarp.registerSocketEvent(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(),
//        		new TokenReceiveSuccessProtoProcessor());
        
        bootstarp.start();
        logger.info(" start success ");
    }
}

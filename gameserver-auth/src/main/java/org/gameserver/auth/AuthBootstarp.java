package org.gameserver.auth;

import com.jyg.enums.ProtoEnum;
import com.jyg.net.HttpService;
import com.jyg.proto.p_auth_sm.p_auth_sm_request_send_token;
import com.jyg.startup.GameServerBootstarp;
import com.jyg.startup.InnerClient;

import io.netty.channel.Channel;

/**
 * Hello world!
 *
 */
public class AuthBootstarp
{
    public static void main ( String[] args ) throws Exception 
    {
    	
    	InnerClient client = new InnerClient();
    	
    	Channel channel = client.connect("localhost", 9001);
    	
    	
    	GameServerBootstarp bootstarp = new GameServerBootstarp();
        
        bootstarp.registerHttpEvent("/index", new TokenSendHttpProcessor(channel));
        
        bootstarp.registerHttpEvent("/login", new LoginHttpProcessor());
        
        bootstarp.registerHttpEvent("/loginhtml", new LoginHtmlHttpProcessor());
        
        bootstarp.registerSendEventIdByProto(ProtoEnum.P_AUTH_SM_REQUEST_SEND_TOKEN.getEventId(), p_auth_sm_request_send_token.class);
        
        bootstarp.addService(new HttpService(8080,true));
        
        bootstarp.registerSocketEvent(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(), new TokenReceiveSuccessProtoProcessor());
        
        
        bootstarp.start();
    }
}

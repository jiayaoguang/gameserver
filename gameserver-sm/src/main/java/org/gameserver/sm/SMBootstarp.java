package org.gameserver.sm;

import com.jyg.enums.ProtoEnum;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.net.Service;
import com.jyg.net.SocketService;
import com.jyg.proto.p_auth_sm.p_auth_sm_request_send_token;
import com.jyg.proto.p_auth_sm.p_sm_auth_response_receive_token;
import com.jyg.proto.p_sm_scene.p_sm_scene_chat;
import com.jyg.startup.GameServerBootstarp;


/**
 * Hello world!
 *
 */
public class SMBootstarp 
{
    public static void main( String[] args ) throws Exception{
        GameServerBootstarp bootstarp = new GameServerBootstarp();
        
        
        ProtoProcessor<p_auth_sm_request_send_token> getTokenProcessor = new ProtoProcessor<p_auth_sm_request_send_token>(p_auth_sm_request_send_token.getDefaultInstance()) {
        	@Override
			public void processProtoMessage(p_auth_sm_request_send_token msg, ProtoResponse response) {
        		System.out.println("i just get token , id" +msg.getRequestId());
        		p_sm_auth_response_receive_token.Builder builder = p_sm_auth_response_receive_token.newBuilder();
        		builder.setRequestId(msg.getRequestId());
        		response.writeMsg( builder);
        	}
			
        };
        
		System.out.println(getTokenProcessor.getProtoClassName());
        
        bootstarp.registerSocketEvent(ProtoEnum.P_AUTH_SM_REQUEST_SEND_TOKEN.getEventId(), getTokenProcessor);
        
        
        bootstarp.registerSendEventIdByProto(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(), p_sm_auth_response_receive_token.class);
        
        
//        
        Service socketService = new SocketService(9001);
        
        bootstarp.addService(socketService);
        
        bootstarp.start();
    }
}

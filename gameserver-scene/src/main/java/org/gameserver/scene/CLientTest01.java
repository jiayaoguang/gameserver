package org.gameserver.scene;

import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.net.ProtoProcessor;
import com.jyg.proto.p_sm_scene;
import com.jyg.proto.p_sm_scene.p_scene_sm_response_pong;
import com.jyg.proto.p_sm_scene.p_sm_scene_chat;
import com.jyg.proto.p_sm_scene.p_sm_scene_request_ping;
import com.jyg.startup.Client;


/**
 * Hello world!
 *
 */
public class CLientTest01
{
    public static void main( String[] args ) throws Exception
    {
    	
    	ProtoProcessor<p_sm_scene_request_ping> pingProcessor = new ProtoProcessor<p_sm_scene_request_ping>(p_sm_scene_request_ping.getDefaultInstance()) {
        	@Override
        	public MessageLiteOrBuilder createResponseMessage(p_sm_scene_request_ping msg ) {
        		System.out.println("i just think so");
        		return p_scene_sm_response_pong.newBuilder();
        	}
			
        };
        ProtoProcessor<p_sm_scene_chat> chatProcessor = new ProtoProcessor<p_sm_scene_chat>(p_sm_scene_chat.getDefaultInstance()) {
        	@Override
        	public MessageLiteOrBuilder createResponseMessage(p_sm_scene_chat msg ) {
        		System.out.println("i just think so");
        		
        		return p_sm_scene_chat.newBuilder().setMsg("hello world!");
        	}
			
        };
        
        
    	
        Client client = new Client();
        
        client.registerRpcEvent(1, pingProcessor);
        
        client.registerRpcEvent(2, chatProcessor);
        
        client.connect("localhost",8080);
        
//        EventDispatcher.getInstance().registerRpcEvent(1, processor);
        
        client.write(p_sm_scene_chat.newBuilder().setMsg("hello world!"));
        
        
//        client.close();
    }
}

package org.gameserver.sm;

import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.RpcService;
import com.jyg.net.Service;
import com.jyg.proto.p_sm_scene.p_scene_sm_response_pong;
import com.jyg.proto.p_sm_scene.p_sm_scene_chat;
import com.jyg.proto.p_sm_scene.p_sm_scene_request_ping;
import com.jyg.startup.Bootstarp;


/**
 * Hello world!
 *
 */
public class ServerTest01 
{
    public static void main( String[] args ) throws Exception{
        Bootstarp bootstarp = new Bootstarp();
        
        
        ProtoProcessor<p_sm_scene_request_ping> processor = new ProtoProcessor<p_sm_scene_request_ping>(p_sm_scene_request_ping.getDefaultInstance()) {
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
        		
        		System.out.println(msg.getMsg());
        		
        		return p_sm_scene_chat.newBuilder().setMsg("hello world too");
        	}
			
        };
        
		System.out.println(processor.getProtoClassName());
        
        bootstarp.registerRpcEvent(1, processor);
        
        bootstarp.registerRpcEvent(2, chatProcessor);
//        
        Service rpcService = new RpcService(8080);
        
        bootstarp.addService(rpcService);
        
        bootstarp.start();
    }
}

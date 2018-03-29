package com.jyg.test01.chat;

import com.google.protobuf.MessageLiteOrBuilder;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.proto.p_sm_scene.p_scene_sm_chat;
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
			public void processProtoMessage(p_sm_scene_request_ping msg, ProtoResponse response) {
				System.out.println("receive ping msg");
				response.writeMsg(p_scene_sm_response_pong.newBuilder());
				
			}
			
        };
        ProtoProcessor<p_sm_scene_chat> receiveChatProcessor = new ProtoProcessor<p_sm_scene_chat>(p_sm_scene_chat.getDefaultInstance()) {
        	int num = 0;
			@Override
			public void processProtoMessage(p_sm_scene_chat msg, ProtoResponse response) {
				System.out.println("receive msg "+num+":"+msg.getMsg());
				num++;
				if(num==10) {
					response.writeMsg( p_scene_sm_chat.newBuilder().setMsg("bye"));
					return;
				}
				response.writeMsg( p_scene_sm_chat.newBuilder().setMsg("hello world!"));
			}
			
        };
        
        
    	
        Client client = new Client();
        
        client.registerSocketEvent(1, pingProcessor);
        
        client.registerSendEventIdByProto(2, p_scene_sm_response_pong.class);
        
        client.registerSendEventIdByProto(3, p_scene_sm_chat.class);
        
        client.registerSocketEvent(4, receiveChatProcessor);
        
        client.connect("localhost",8080);
        
        
        client.write(p_scene_sm_chat.newBuilder().setMsg("hello world!"));
        
        
//        client.close();
    }
}

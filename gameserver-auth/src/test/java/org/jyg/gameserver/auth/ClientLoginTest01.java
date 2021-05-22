package org.jyg.gameserver.auth;

import org.junit.Test;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.proto.MsgLogin;
import org.jyg.gameserver.proto.MsgLoginReply;
import org.jyg.gameserver.proto.MsgLoginRequest;

/**
 * Hello world!
 *
 */
public class ClientLoginTest01
{
    public static void main( String[] args ) throws Exception
    {

		ProtoProcessor<MsgLoginReply> chatProcessor = new ProtoProcessor<MsgLoginReply>(MsgLoginReply.getDefaultInstance()) {
			@Override
			public void process(Session session, MsgLoginReply msg) {

				System.out.println(msg.getToken() );

//				session.writeMessage(p_test.p_sm_scene_chat.newBuilder().setMsg("i just think so ,hello world too").build());
			}

		};
        
        
    	
        TcpClient client = new TcpClient("localhost",8081);
//		client.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		client.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		client.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		client.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

		client.addMsgId2ProtoMapping(1000 , MsgLoginRequest.class);
        client.addMsgId2ProtoMapping(1001 , MsgLoginReply.class);

		client.addProtoProcessor(1001 , chatProcessor);

        client.start();
//        client.connect("localhost",8081);

        client.write(MsgLoginRequest.newBuilder().setAccount("jyg").build());

        Thread.sleep(1000);

//        client.close();
    }
    
}

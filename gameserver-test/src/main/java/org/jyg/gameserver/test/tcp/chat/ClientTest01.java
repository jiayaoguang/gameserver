package org.jyg.gameserver.test.tcp.chat;

import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.proto.p_sm_scene.p_scene_sm_chat;
import org.jyg.gameserver.proto.p_sm_scene.p_sm_scene_chat;
import org.jyg.gameserver.test.proto.MsgChat;
import org.jyg.gameserver.test.proto.p_test;
import org.jyg.gameserver.test.proto.p_test.p_scene_sm_response_pong;
import org.jyg.gameserver.test.proto.p_test.p_sm_scene_request_ping;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.junit.Test;

/**
 * Hello world!
 *
 */
public class ClientTest01
{
    public static void main( String[] args ) throws Exception
    {

		ProtoProcessor<MsgChat> chatProcessor = new ProtoProcessor<MsgChat>(MsgChat.getDefaultInstance()) {
			@Override
			public void process(Session session, MsgChat msg) {

				System.out.println(msg.getContent() );
				if("bye".equals(msg.getContent() )) {
					return;
				}

				session.writeMessage(p_test.p_sm_scene_chat.newBuilder().setMsg("i just think so ,hello world too"));
			}

		};
        
        
    	
        TcpClient client = new TcpClient();
//		client.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		client.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		client.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		client.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

		client.addMsgId2ProtoMapping(5, MsgChat.getDefaultInstance());

		client.addProtoProcessor(chatProcessor);

        client.start();
        client.connect("localhost",8080);

        client.write(MsgChat.newBuilder().setContent("hello world!").build());

        Thread.sleep(1000);

//        client.close();
    }
    
    @Test
    public void test01() {
    	char u = '　';
    	System.out.println((int)(char)(u*100));
    }
}

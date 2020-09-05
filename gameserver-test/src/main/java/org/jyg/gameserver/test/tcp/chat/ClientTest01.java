package org.jyg.gameserver.test.tcp.chat;

import org.junit.Test;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.test.proto.MsgChat;

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

//				session.writeMessage(p_test.p_sm_scene_chat.newBuilder().setMsg("i just think so ,hello world too").build());
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
        client.connect("localhost",8088);
		for(int i=0;i<50;i++){
			client.write(MsgChat.newBuilder().setContent("hello world!").build());
			Thread.sleep(1000);
		}



        client.close();
		client.stop();
    }
    
    @Test
    public void test01() {
    	char u = '　';
    	System.out.println((int)(char)(u*100));
    }
}

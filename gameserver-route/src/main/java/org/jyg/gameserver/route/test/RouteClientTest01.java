package org.jyg.gameserver.route.test;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.route.msg.ChatMsgObj;
import org.jyg.gameserver.route.msg.ChatReplyMsgObj;

/**
 * Hello world!
 *
 */
public class RouteClientTest01
{
    public static void main( String[] args ) throws Exception
    {

		ByteMsgObjProcessor<ChatReplyMsgObj> chatProcessor = new ByteMsgObjProcessor<ChatReplyMsgObj>() {


			@Override
			public void process(Session session, EventData<ChatReplyMsgObj> event) {

			}
		};
        
        
    	
        TcpClient client = new TcpClient();
//		client.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		client.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		client.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		client.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

		client.getContext().addMsgId2MsgClassMapping(1001, ChatMsgObj.class);
		client.getContext().addMsgId2MsgClassMapping(1002, ChatReplyMsgObj.class);

		client.addByteMsgObjProcessor(chatProcessor);

        client.start();
        client.connect("localhost",8081);
		for(int i=0;i<50;i++){
			ChatMsgObj chatMsgObj = new ChatMsgObj();
			chatMsgObj.setContent("hello world!" + i);
			client.write(chatMsgObj);
			Thread.sleep(1);
		}



        client.close();
		client.stop();
    }
    
}

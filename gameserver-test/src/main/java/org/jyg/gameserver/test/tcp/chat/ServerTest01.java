package org.jyg.gameserver.test.tcp.chat;

import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.test.proto.MsgChat;

import java.util.ArrayList;
import java.util.List;


/**
 * Hello world!
 *
 */
public class ServerTest01 
{
    public static void main( String[] args ) throws Exception{
        GameServerBootstrap bootstarp = new GameServerBootstrap();
        
        ProtoProcessor<MsgChat> chatProcessor = new ProtoProcessor<MsgChat>(MsgChat.getDefaultInstance()) {
        	@Override
			public void process(Session session, MsgChat msg) {
        		
        		System.out.println(msg.getContent() );
        		if("bye".equals(msg.getContent() )) {
        			return;
        		}

//				List<MsgChat> msgList = new ArrayList<>();
//
//        		for(int i = 0;i<2;i++){
//        			msgList.add(MsgChat.newBuilder().setContent("i just think so ,hello world too -"+i).build());
//				}
//				session.writeMessage(msgList);

				session.writeMessage(MsgChat.newBuilder().setContent("i just think so ,hello world too").build());
			}
			
        };
//		bootstarp.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		bootstarp.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

		bootstarp.addMsgId2ProtoMapping(105, MsgChat.getDefaultInstance());


		bootstarp.addProtoProcessor(chatProcessor);
        

		bootstarp.addTcpService(8088);
        
        bootstarp.start();
    }
}

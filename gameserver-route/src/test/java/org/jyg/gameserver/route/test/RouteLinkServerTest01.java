package org.jyg.gameserver.route.test;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.route.test.msg.ChatMsgObj;


/**
 * Hello world!
 *
 */
public class RouteLinkServerTest01
{
    public static void main( String[] args ) throws Exception{
        GameServerBootstrap bootstarp = new GameServerBootstrap();

		ByteMsgObjProcessor<ChatMsgObj> chatProcessor = new ByteMsgObjProcessor<ChatMsgObj>() {
			@Override
			public void process(Session session, EventData<ChatMsgObj> event) {
				AllUtil.println("channel :" + session.getRemoteAddr() + " , get chat msg : " + event.getData().getContent());
			}


        };
//		bootstarp.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		bootstarp.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());


		bootstarp.getGameContext().addMsgId2MsgClassMapping(1001, ChatMsgObj.class);



		bootstarp.addByteMsgObjProcessor(chatProcessor);
        

		bootstarp.addTcpConnector(8088);
        
        bootstarp.start();
    }
}

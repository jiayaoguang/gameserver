package org.jyg.gameserver.test.udp;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.UdpConnector;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.test.proto.MsgChat;


/**
 * Hello world!
 */
public class UdpServerTest01 {
    public static void main(String[] args) throws Exception {
        GameServerBootstrap bootstarp = new GameServerBootstrap();
        bootstarp.getGameContext().getServerConfig().setNettyIOThreadNum(10);

//		bootstarp.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		bootstarp.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());
        bootstarp.getGameContext().addMsgId2MsgClassMapping(1234,UdpTestMsg.class);

        bootstarp.getDefaultConsumer().addProcessor(new ByteMsgObjProcessor<UdpTestMsg>() {
            @Override
            public void process(Session session, MsgEvent<UdpTestMsg> event) {
                session.writeMessage(event.getMsgData());
            }
        });

        bootstarp.addConnector(new UdpConnector(45678, bootstarp.getGameContext()));

        bootstarp.start();
    }
}

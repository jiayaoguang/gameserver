package org.jyg.gameserver.test.tcp.jsonMsg;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.msg.PingByteMsg;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.Logs;

/**
 * Hello world!
 */
public class JsonClientTest01 {
    public static void main(String[] args) throws Exception {

        TcpClient client = new TcpClient("localhost", 8088);
//		client.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		client.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		client.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		client.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

        client.getGameContext().addMsgId2MsgClassMapping(108, JsonServerTest01.ChatMsgObj.class);
//		client.getDefaultConsumer().addProcessor(8, chatProcessor);

//		client.addProtoProcessor(chatProcessor);

        ByteMsgObjProcessor<JsonServerTest01.ChatMsgObj> chatProcessor = new ByteMsgObjProcessor<JsonServerTest01.ChatMsgObj>(JsonServerTest01.ChatMsgObj.class) {
            @Override
            public void process(Session session, MsgEvent<JsonServerTest01.ChatMsgObj> event) {
                Logs.DEFAULT_LOGGER.info(" ========================= receive reply json " + event.getMsgData().getConetnt());
//                session.writeMessage(chatMsgObj);
            }
        };
        client.getDefaultConsumer().addProcessor(108, chatProcessor);


        client.start();

        client.write(new PingByteMsg());
//        client.connect("localhost", 8088);
        for (int i = 0; i < 5; i++) {
            JsonServerTest01.ChatMsgObj chatMsgObj = new JsonServerTest01.ChatMsgObj();
            chatMsgObj.setConetnt(i + "---");
            client.write(chatMsgObj);
        }

        Thread.sleep(1000000);

        client.close();
        client.stop();
    }

}

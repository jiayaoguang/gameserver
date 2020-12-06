package org.jyg.gameserver.test.tcp.jsonMsg;

import org.junit.Test;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.test.proto.MsgChat;

/**
 * Hello world!
 */
public class JsonClientTest01 {
    public static void main(String[] args) throws Exception {

        TcpClient client = new TcpClient();
//		client.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		client.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		client.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		client.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

        client.getContext().addMsgId2JsonMsgCLassMapping(8, JsonServerTest01.ChatMsgObj.class);
//		client.getDefaultConsumer().addProcessor(8, chatProcessor);

//		client.addProtoProcessor(chatProcessor);

        client.start();
        client.connect("localhost", 8088);
        for (int i = 0; i < 50; i++) {
            JsonServerTest01.ChatMsgObj chatMsgObj = new JsonServerTest01.ChatMsgObj();
            chatMsgObj.setConetnt(i + "---");
            client.write(chatMsgObj);
        }

        Thread.sleep(10000);

        client.close();
        client.stop();
    }

}

package org.jyg.gameserver.example.ygserver;

import org.apache.logging.log4j.core.config.Configurator;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.example.ygserver.msg.*;

/**
 * Hello world!
 */
public class YgGameClient {
    public static void main(String[] args) throws Exception {

        Configurator.initialize("log4j2_client.xml" , "");

//        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j2_client.xml"));

        TcpClient client = new TcpClient("localhost", 8088);
//		client.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		client.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		client.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		client.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

        client.getContext().addMsgId2JsonMsgClassMapping(108, LoginRequestMsg.class);
        client.getContext().addMsgId2JsonMsgClassMapping(109, LoginReplyMsg.class);
        client.getContext().addMsgId2JsonMsgClassMapping(110, ChatRequestJson.class);
        client.getContext().addMsgId2JsonMsgClassMapping(111, ChatReplyJson.class);

        client.getContext().addMsgId2JsonMsgClassMapping(120, CreateEnemyMsg.class);

        client.getDefaultConsumer().addProcessor( new ChatReplyProcessor());



        client.getContext().getDefaultConsumer().addProcessor( new ByteMsgObjProcessor<LoginReplyMsg>(LoginReplyMsg.class) {

            @Override
            public void process(Session session, EventData<LoginReplyMsg> event) {
                getConsumer().getTimerManager().addUnlimitedTimer(5 * 1000L, 5 * 1000L, () -> {
                    ChatRequestJson chatRequestJson = new ChatRequestJson();
                    chatRequestJson.setContent("hello world");
                    session.writeMessage(chatRequestJson);
                });
            }
        });



        client.start();
//        for (int i = 0; i < 50; i++) {
//            JsonServer.ChatMsgObj chatMsgObj = new JsonServer.ChatMsgObj();
//            chatMsgObj.setConetnt(i + "---");
//            client.write(chatMsgObj);
//        }

        LoginRequestMsg loginRequestMsg = new LoginRequestMsg();
        loginRequestMsg.setName("maik jack");
        loginRequestMsg.setPassword("maik");

        client.write(loginRequestMsg);





//        Thread.sleep(10000);
//
//        client.close();
//        client.stop();
    }

}
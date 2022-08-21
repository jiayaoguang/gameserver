package org.jyg.gameserver.test.mq;

import org.jyg.gameserver.core.consumer.RabbitMQPushGameConsumer;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.net.RabbitMQConnector;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.test.proto.MsgChat;


/**
 * Hello world!
 */
public class MQServerTest01 {
    public static void main(String[] args) throws Exception {
        GameServerBootstrap bootstrap = new GameServerBootstrap();

        ProtoProcessor<MsgChat> chatProcessor = new ProtoProcessor<MsgChat>(MsgChat.getDefaultInstance()) {
            @Override
            public void process(Session session, MsgChat msg) {

                System.out.println(msg.getContent());
                if ("bye".equals(msg.getContent())) {
                    return;
                }

//				List<MsgChat> msgList = new ArrayList<>();
//
//        		for(int i = 0;i<2;i++){
//        			msgList.add(MsgChat.newBuilder().setContent("i just think so ,hello world too -"+i).build());
//				}
//				session.writeMessage(msgList);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                session.writeMessage(MsgChat.newBuilder().setContent("i just think so ,hello world too").build());
            }

        };

        String QUEUE_NAME = "my_queue"; //队列名称
        String EXCHANGE_NAME = "my_exchange"; //要使用的exchange的名称

//		bootstarp.addMsgId2ProtoMapping(1, p_sm_scene_request_ping.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(2, p_scene_sm_response_pong.getDefaultInstance());
//
//		bootstarp.addMsgId2ProtoMapping(3, p_scene_sm_chat.getDefaultInstance());
//		bootstarp.addMsgId2ProtoMapping(4, p_sm_scene_chat.getDefaultInstance());

        bootstrap.addMsgId2ProtoMapping(105, MsgChat.getDefaultInstance());


        bootstrap.addProtoProcessor(chatProcessor);


        RabbitMQPushGameConsumer mqConsumer = new RabbitMQPushGameConsumer(QUEUE_NAME, EXCHANGE_NAME);
        bootstrap.getGameContext().getConsumerManager().addConsumer(mqConsumer);


        bootstrap.addConnector(new RabbitMQConnector(bootstrap.getGameContext(), 0, QUEUE_NAME, EXCHANGE_NAME));
        bootstrap.start();


        bootstrap.getGameContext().getConsumerManager().publicEvent(mqConsumer.getId() , EventType.DEFAULT_EVENT  , MsgChat.newBuilder().setContent("1111").setId(1).build(),0);
        bootstrap.getGameContext().getConsumerManager().publicEvent(mqConsumer.getId() , EventType.DEFAULT_EVENT  , MsgChat.newBuilder().setContent("1111").setId(1).build(),0);



    }
}

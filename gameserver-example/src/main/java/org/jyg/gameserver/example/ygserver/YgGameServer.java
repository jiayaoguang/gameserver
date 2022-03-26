package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.msg.EmptyMsgCodec;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.processor.SysInfoHttpProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBConsumerGroup;
import org.jyg.gameserver.example.ygserver.msg.*;



/**
 * Hello world!
 */
public class YgGameServer {

    public static void main(String[] args) throws Exception {

//        CreateTableUtil.createTable(PlayerDB.class);
        GameServerBootstrap bootstarp = new GameServerBootstrap();


        DBConsumerGroup dbConsumerGroup = new DBConsumerGroup();


        bootstarp.getContext().getConsumerManager().addConsumer(dbConsumerGroup);
        bootstarp.getContext().getDefaultConsumer().getInstanceManager().putInstance(new ConsumerDBManager(bootstarp.getDefaultConsumer(), dbConsumerGroup.getId()));

        bootstarp.getContext().getDefaultConsumer().getInstanceManager().putInstance(new PlayerManager());
        bootstarp.getContext().getDefaultConsumer().getInstanceManager().putInstance(FrameManager.class);
        bootstarp.getContext().getDefaultConsumer().getInstanceManager().putInstance(RoomManager.class);
//
//        dbConsumerGroup.addTableInfo(PlayerDB.class);



//        bootstarp.getContext().addMsgId2JsonMsgCLassMapping(8, ChatMsgObj.class);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(108, LoginRequestMsg.class);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(109, LoginReplyMsg.class);

        bootstarp.getContext().addMsgId2JsonMsgClassMapping(110, ChatRequestJson.class);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(111, ChatReplyJson.class);

        bootstarp.getContext().addMsgId2JsonMsgClassMapping(120, SCPlayerJoinMsg.class);

        bootstarp.getContext().addMsgId2JsonMsgClassMapping(121, ClientFrameMsg.class);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(122, ServerFrameMsg.class);


        bootstarp.getContext().addMsgId2JsonMsgClassMapping(123, CSEnterRoomMsg.class);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(124, SCEnterRoomMsg.class);


        bootstarp.getContext().addMsgId2JsonMsgClassMapping(125, CSHitMsg.class);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(126, SCHitMsg.class);


        bootstarp.getContext().addMsgId2JsonMsgClassMapping(127, SCRoomEndMsg.class );

        bootstarp.getContext().addByteMsgCodec(new EmptyMsgCodec(new SCRoomEndMsg()));



        ByteMsgObjProcessor<LoginRequestMsg> loginProcessor = new LoginProcessor();
        bootstarp.getDefaultConsumer().addProcessor(loginProcessor);
        bootstarp.getDefaultConsumer().addProcessor( new ChatRequestProcessor());
        bootstarp.getDefaultConsumer().addProcessor( new CreateTableProcessor());
        bootstarp.getDefaultConsumer().addProcessor( new ClientFrameProcessor());
        bootstarp.getDefaultConsumer().addProcessor( new EnterRoomProcessor());


        bootstarp.getDefaultConsumer().addProcessor( new CSHitProcessor());

        bootstarp.addTcpConnector(8088);

//        bootstarp.addHttpConnector(80);
        bootstarp.addHttpConnector(8888);

        bootstarp.getDefaultConsumer().getEventManager().addEvent(new ConsumerThreadStartEvent((consumer, b)->{

            consumer.getTimerManager().addTimer(-1,20 , ()->{
//                consumer.getChannelManager().broadcast(new CreateEnemyMsg());
//                Logs.DEFAULT_LOGGER.info(" send CreateEnemyMsg ...................... ");

                PlayerManager playerManager = consumer.getInstance(PlayerManager.class);
                FrameManager frameManager = consumer.getInstance(FrameManager.class);

                ServerFrameMsg serverFrameMsg = new ServerFrameMsg();

                serverFrameMsg.getPlayerFrameMsgs().addAll(frameManager.getPlayerFrameMsgMap().values());

//                PlayerFrameMsg robotFrameMsg = new PlayerFrameMsg();
//                robotFrameMsg.setPlayerId(100);
//                Vector2Msg vector2Msg = new Vector2Msg();
//                vector2Msg.setX(0);
//                vector2Msg.setY(0);
//                robotFrameMsg.setPosi(vector2Msg);
//
//                serverFrameMsg.getPlayerFrameMsgs().add(robotFrameMsg);

                for(RoomPlayer player:  consumer.getInstanceManager().getInstance(RoomManager.class).getRoom().getRoomPlayerMap().values() ){


                    Session session = player.getPlayer().getSession();

                    if(session == null || !session.getChannel().isOpen()){
                        continue;
                    }
                    session.writeMessage(serverFrameMsg);
                }

//                for(Map.Entry<Long, PlayerFrameMsg> entry : frameManager.getPlayerFrameMsgMap().entrySet() ){
//                    long sessionId = entry.getKey() ;
//
//
//                }


            });

        }));


        bootstarp.start();



    }
}

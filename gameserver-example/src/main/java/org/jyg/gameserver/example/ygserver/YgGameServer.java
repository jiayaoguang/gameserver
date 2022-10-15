package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.msg.EmptyMsgCodec;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBGameConsumerGroup;
import org.jyg.gameserver.example.ygserver.msg.*;



/**
 * Hello world!
 */
public class YgGameServer {

    public static void main(String[] args)  {

//        CreateTableUtil.createTable(PlayerDB.class);
        GameServerBootstrap bootstarp = new GameServerBootstrap();


        DBGameConsumerGroup dbConsumerGroup = new DBGameConsumerGroup();


        bootstarp.getGameContext().getConsumerManager().addConsumer(dbConsumerGroup);
        bootstarp.getGameContext().getDefaultGameConsumer().getInstanceManager().putInstance(new ConsumerDBManager(bootstarp.getDefaultConsumer(), dbConsumerGroup.getId()));

        bootstarp.getGameContext().getDefaultGameConsumer().getInstanceManager().putInstance(new PlayerManager());
//        bootstarp.getGameContext().getDefaultGameConsumer().getInstanceManager().putInstance(FrameManager.class);
        bootstarp.getGameContext().getDefaultGameConsumer().getInstanceManager().putInstance(RoomManager.class);
//
//        dbConsumerGroup.addTableInfo(PlayerDB.class);



//        bootstarp.getContext().addMsgId2MsgClassMapping(8, ChatMsgObj.class);
        bootstarp.getGameContext().addMsgId2MsgClassMapping(108, LoginRequestMsg.class);
        bootstarp.getGameContext().addMsgId2MsgClassMapping(109, LoginReplyMsg.class);

        bootstarp.getGameContext().addMsgId2MsgClassMapping(110, ChatRequestJson.class);
        bootstarp.getGameContext().addMsgId2MsgClassMapping(111, ChatReplyJson.class);

        bootstarp.getGameContext().addMsgId2MsgClassMapping(120, SCPlayerJoinMsg.class);

        bootstarp.getGameContext().addMsgId2MsgClassMapping(121, ClientFrameMsg.class);
        bootstarp.getGameContext().addMsgId2MsgClassMapping(122, ServerFrameMsg.class);


        bootstarp.getGameContext().addMsgId2MsgClassMapping(123, CSEnterRoomMsg.class);
        bootstarp.getGameContext().addMsgId2MsgClassMapping(124, SCEnterRoomMsg.class);


        bootstarp.getGameContext().addMsgId2MsgClassMapping(125, CSHitMsg.class);
        bootstarp.getGameContext().addMsgId2MsgClassMapping(126, SCHitMsg.class);


        bootstarp.getGameContext().addMsgId2MsgClassMapping(127, SCRoomEndMsg.class );
        bootstarp.getGameContext().addByteMsgCodec(new EmptyMsgCodec(new SCRoomEndMsg()));

        bootstarp.getGameContext().addMsgId2MsgClassMapping(128, CSCreateMotionMsg.class );
        bootstarp.getGameContext().addMsgId2MsgClassMapping(129, SCCreateMotionMsg.class );

        bootstarp.getGameContext().addMsgId2MsgClassMapping(130, SCTipMsg.class );
        bootstarp.getGameContext().addMsgId2MsgClassMapping(131, SCMotionDeadMsg.class );

        bootstarp.getGameContext().addMsgId2MsgClassMapping(132, CSEatScoreMotionMsg.class );
        bootstarp.getGameContext().addMsgId2MsgClassMapping(133, SCUpdatePlayerScoreMsg.class );


        ByteMsgObjProcessor<LoginRequestMsg> loginProcessor = new LoginProcessor();
        bootstarp.getDefaultConsumer().addProcessor(loginProcessor);
        bootstarp.getDefaultConsumer().addProcessor( new ChatRequestProcessor());
        bootstarp.getDefaultConsumer().addProcessor( new CreateTableProcessor());
        bootstarp.getDefaultConsumer().addProcessor( new ClientFrameProcessor());
        bootstarp.getDefaultConsumer().addProcessor( new EnterRoomProcessor());
        bootstarp.getDefaultConsumer().addProcessor( new CreateMotionProcessor());

        bootstarp.getDefaultConsumer().addProcessor( new CSEatScoreMotionProcessor());




        bootstarp.getDefaultConsumer().addProcessor( new CSHitProcessor());

        bootstarp.addTcpConnector(8088);

        bootstarp.addHttpConnector(80);
        bootstarp.addHttpConnector(8888);

//        bootstarp.getDefaultConsumer().getEventManager().addEvent(new ConsumerThreadStartEvent((consumer, b)->{
//
//            consumer.getTimerManager().addTimer(-1,20 , ()->{
////                consumer.getChannelManager().broadcast(new CreateEnemyMsg());
////                Logs.DEFAULT_LOGGER.info(" send CreateEnemyMsg ...................... ");
//
////                PlayerManager playerManager = consumer.getInstance(PlayerManager.class);
//                FrameManager frameManager = consumer.getInstance(FrameManager.class);
//
//                ServerFrameMsg serverFrameMsg = new ServerFrameMsg();
//
//                serverFrameMsg.getPlayerFrameMsgs().addAll(frameManager.getPlayerFrameMsgMap().values());
//
////                PlayerFrameMsg robotFrameMsg = new PlayerFrameMsg();
////                robotFrameMsg.setPlayerId(100);
////                Vector2Msg vector2Msg = new Vector2Msg();
////                vector2Msg.setX(0);
////                vector2Msg.setY(0);
////                robotFrameMsg.setPosi(vector2Msg);
////
////                serverFrameMsg.getPlayerFrameMsgs().add(robotFrameMsg);
//
//                for(RoomPlayer player:  consumer.getInstanceManager().getInstance(RoomManager.class).getRoom().getRoomPlayerMap().values() ){
//
//
//                    Session session = player.getPlayer().getSession();
//
//                    if(session == null || !session.isOpen()){
//                        continue;
//                    }
//                    session.writeMessage(serverFrameMsg);
//                }
//
////                for(Map.Entry<Long, PlayerFrameMsg> entry : frameManager.getPlayerFrameMsgMap().entrySet() ){
////                    long sessionId = entry.getKey() ;
////
////
////                }
//
//
//            });
//
//        }));

        try {
            bootstarp.start();
        }catch (Exception e){
            e.printStackTrace();
            bootstarp.stop();
        }



    }
}

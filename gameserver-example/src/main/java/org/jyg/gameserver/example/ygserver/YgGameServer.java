package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.session.SessionState;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.IdUtil;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.db.DBConsumerGroup;
import org.jyg.gameserver.example.ygserver.msg.*;

import java.util.List;
import java.util.Random;


/**
 * Hello world!
 */
public class YgGameServer {

    public static void main(String[] args) throws Exception {


        GameServerBootstrap bootstarp = new GameServerBootstrap();


        DBConsumerGroup dbConsumerGroup = new DBConsumerGroup();

        bootstarp.getContext().getConsumerManager().addConsumer(dbConsumerGroup);
        bootstarp.getContext().getDefaultConsumer().getInstanceManager().putInstance(new ConsumerDBManager(bootstarp.getDefaultConsumer(), dbConsumerGroup.getId()));
        bootstarp.getContext().getDefaultConsumer().getInstanceManager().putInstance(new PlayerManager());
//
//        dbConsumerGroup.addTableInfo(PlayerDB.class);

        ByteMsgObjProcessor<LoginRequestMsg> loginProcessor = new ByteMsgObjProcessor<LoginRequestMsg>(LoginRequestMsg.class) {
            @Override
            public void process(Session session, EventData<LoginRequestMsg> event) {
                ConsumerDBManager consumerDBManager = getConsumer().getInstanceManager().getInstance(ConsumerDBManager.class);

                LoginRequestMsg loginRequestMsg = event.getData();

                PlayerDB playerDB = new PlayerDB();
                playerDB.setName(event.getData().getName());

                if(session.getSessionState() == SessionState.WAIR_REPLY){
                    return;
                }


                consumerDBManager.selectBy(playerDB,"name", new ResultHandler<List<PlayerDB>>() {
                    @Override
                    public void call(int eventId, List<PlayerDB> playerDBList) {
//                        List<PlayerDB> playerDBList = (List<PlayerDB>)data;

                        try {

                            int errorCode = 0;

                            PlayerDB currentPlayerDB;

                            if(playerDBList.size() == 0){
                                currentPlayerDB = new PlayerDB();
                                currentPlayerDB.setId(IdUtil.nextId());
                                currentPlayerDB.setName(loginRequestMsg.getName());
                                currentPlayerDB.setPassword(loginRequestMsg.getPassword());
                                consumerDBManager.insert(currentPlayerDB);

                            }else {
                                currentPlayerDB = playerDBList.get(0);


                                if(!currentPlayerDB.getPassword().equals(loginRequestMsg.getPassword())){
                                    errorCode = 1;
                                }

                            }

                            LoginReplyMsg loginReplyMsg = new LoginReplyMsg();
                            loginReplyMsg.setId(currentPlayerDB.getId());
                            loginReplyMsg.setName(currentPlayerDB.getName());
                            loginReplyMsg.setErrorCode(errorCode);

                            Random random = new Random();
                            for(int num = 0 ;num < 200;num++){
                                WallMsg wallMsg = new WallMsg();
//                            Vector2Msg vector2Msg = new Vector2Msg();
//                            vector2Msg.setX(20);
//                            vector2Msg.setY(30);
                                wallMsg.setX(random.nextInt(600) - 300);
                                wallMsg.setY(random.nextInt(600) - 300);
                                wallMsg.setHeight(random.nextInt(100) - 50);
                                wallMsg.setWidth(random.nextInt(100) - 50);
//                            wallMsg.setPosi(vector2Msg);
                                loginReplyMsg.wallMsgs.add(wallMsg);
                            }

                            session.setSessionObject(currentPlayerDB);
                            session.writeMessage(loginReplyMsg);

                        }finally {

                            session.setSessionState(SessionState.NORMAL);

                        }



                    }

                    @Override
                    public void onTimeout() {
                        LoginReplyMsg loginReplyMsg = new LoginReplyMsg();
                        loginReplyMsg.setErrorCode(2);

                        session.writeMessage(loginReplyMsg);
                        session.setSessionState(SessionState.NORMAL);
                    }
                });

            }
        };


//        bootstarp.getContext().addMsgId2JsonMsgCLassMapping(8, ChatMsgObj.class);
        bootstarp.getDefaultConsumer().addProcessor(108, loginProcessor);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(109, LoginReplyMsg.class);

        bootstarp.getContext().addMsgId2JsonMsgClassMapping(110, ChatRequestJson.class);
        bootstarp.getContext().addMsgId2JsonMsgClassMapping(111, ChatReplyJson.class);

        bootstarp.getContext().addMsgId2JsonMsgClassMapping(120, CreateEnemyMsg.class);

        bootstarp.getDefaultConsumer().addProcessor( new ChatRequestProcessor());

        bootstarp.addTcpConnector(8088);


//        bootstarp.getDefaultConsumer().setConsumerStartHandler(( consumer)->{
//            consumer.getTimerManager().addTimer(-1,5000L , ()->{
//                consumer.getChannelManager().broadcast(new CreateEnemyMsg());
//                Logs.DEFAULT_LOGGER.info(" send CreateEnemyMsg ...................... ");
//            });
//        });

        bootstarp.start();
    }
}

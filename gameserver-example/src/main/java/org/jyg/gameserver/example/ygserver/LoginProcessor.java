package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.session.SessionState;
import org.jyg.gameserver.core.util.IdUtil;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.example.ygserver.msg.*;

import java.util.List;
import java.util.Random;

/**
 * create by jiayaoguang on 2021/9/11
 */
public class LoginProcessor extends ByteMsgObjProcessor<LoginRequestMsg> {


    public LoginProcessor() {
        super(LoginRequestMsg.class);
    }

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
                    for(int num = 0 ;num < 300;num++){
                        WallMsg wallMsg = new WallMsg();
                        Vector2Msg vector2Msg = new Vector2Msg();
                        vector2Msg.setX(random.nextInt(600) - 300);
                        vector2Msg.setY(random.nextInt(600) - 300);
//                                wallMsg.setX(random.nextInt(600) - 300);
//                                wallMsg.setY(random.nextInt(600) - 300);
                        wallMsg.setHeight(random.nextInt(100));
                        wallMsg.setWidth(random.nextInt(100));
                        wallMsg.setPosi(vector2Msg);
                        loginReplyMsg.wallMsgs.add(wallMsg);
                    }

                    session.setSessionObject(currentPlayerDB);
                    session.writeMessage(loginReplyMsg);

                    PlayerManager playerManager = getConsumer().getInstance(PlayerManager.class);
                    Player player = new Player();
                    player.setPlayerDB(currentPlayerDB);
                    player.setSessionId(session.getSessionId());
                    playerManager.getLoginPlayerMap().put(session.getSessionId(), player);

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
}
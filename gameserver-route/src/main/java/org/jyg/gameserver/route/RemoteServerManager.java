package org.jyg.gameserver.route;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.event.InnerMsgEvent;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang on 2022/8/14
 */
public class RemoteServerManager implements Lifecycle {

    private final int remoteConsumerId;
    private final GameContext gameContext;
    public RemoteServerManager(GameContext gameContext, int remoteConsumerId) {
        this.remoteConsumerId = remoteConsumerId;
        this.gameContext = gameContext;
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    public void sendRemoteMsg(MessageLite message){
        InnerMsgEvent innerMsgEvent = new InnerMsgEvent( 0,message );

        this.gameContext.getConsumerManager().publicEvent(remoteConsumerId, innerMsgEvent);
    }


    /**
     * TODO 改为 publicEvent
     */
    public void sendRemoteMsg(ByteMsgObj message){

        InnerMsgEvent innerMsgEvent = new InnerMsgEvent( 0,message  );

        this.gameContext.getConsumerManager().publicEvent(remoteConsumerId , innerMsgEvent);
    }


}

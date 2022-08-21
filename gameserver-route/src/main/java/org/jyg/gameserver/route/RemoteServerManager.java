package org.jyg.gameserver.route;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.enums.EventType;
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
        this.gameContext.getConsumerManager().publicEvent(remoteConsumerId , EventType.UNKNOWN , message ,0);
    }

    public void sendRemoteMsg(ByteMsgObj message){
        this.gameContext.getConsumerManager().publicEvent(remoteConsumerId , EventType.UNKNOWN , message ,0);
    }


}

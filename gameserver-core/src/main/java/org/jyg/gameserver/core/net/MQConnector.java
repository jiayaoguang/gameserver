package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.event.MQMsgEvent;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.session.MQSession;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.Arrays;

public abstract class MQConnector extends AbstractConnector {


    private final int mqPushConsumerId;

    private final Session mqSession;



    public MQConnector(GameContext gameContext, int mqPushConsumerId ) {
        super(gameContext);
        this.mqPushConsumerId = mqPushConsumerId;
        this.mqSession = new MQSession(mqPushConsumerId, gameContext);
    }






    protected final void publicToDefault(String topic , String key ,byte[] msg){

        int msgId = 0;

        msgId |= (msg[0]&0xff);
        msgId <<=8;
        msgId |= (msg[1]&0xff);
        msgId <<=8;
        msgId |= (msg[2]&0xff);
        msgId <<=8;
        msgId |= (msg[3]&0xff);

        AbstractMsgCodec<?> msgCodec = getGameContext().getMsgCodec(msgId);
        if (msgCodec == null) {
            Logs.CONSUMER.error(" protoParser not found ,id : {} ", msgId);
            return;
        }


        Object msgObj = null;
        try {
            msgObj = msgCodec.decode(Arrays.copyOfRange(msg, 4, msg.length));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        MQMsgEvent mqMsgEvent = new MQMsgEvent(msgId , msgObj  ,mqSession );

        getGameContext().getConsumerManager().publicEvent(gameContext.getMainConsumerId(), mqMsgEvent);

    }


}

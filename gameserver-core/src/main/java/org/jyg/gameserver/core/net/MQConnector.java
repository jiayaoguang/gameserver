package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.consumer.MpscQueueConsumer;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.manager.ConsumerManager;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;

import java.util.Arrays;
import java.util.List;

public abstract class MQConnector extends AbstractConnector {


    private int mqPushConsumerId;



    public MQConnector(Context context , int mqPushConsumerId ) {
        super(context);
        this.mqPushConsumerId = mqPushConsumerId;

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

        AbstractMsgCodec<?> msgCodec = getContext().getMsgCodec(msgId);
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

        getContext().getConsumerManager().publicEvent(ConsumerManager.DEFAULT_CONSUMER_ID, EventType.MQ_MSG_COME, msgObj, null, msgId, new EventExtData(mqPushConsumerId, 0));

    }


}

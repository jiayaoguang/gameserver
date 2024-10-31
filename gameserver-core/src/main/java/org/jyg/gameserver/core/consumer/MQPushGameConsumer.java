package org.jyg.gameserver.core.consumer;

import com.google.protobuf.MessageLite;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.Logs;

public abstract class MQPushGameConsumer extends MpscQueueGameConsumer {



    public MQPushGameConsumer() {

    }



    public void doStart(){
        super.doStart();

    }



    @Override
    public void processDefaultEvent(ConsumerDefaultEvent eventData) {

        int eventId = eventData.getEventId();

        int msgId = 0;

        Object msgObj = eventData.getData();
        if(msgObj instanceof ByteMsgObj){
            msgId = getGameContext().getMsgIdByByteMsgObj((Class<? extends ByteMsgObj>) msgObj.getClass());
        }else if(msgObj instanceof MessageLite){
            msgId = getGameContext().getMsgIdByProtoClass((Class<? extends MessageLite>) msgObj.getClass());
        }else {
            Logs.DEFAULT_LOGGER.error("msgObj {} not found msgId",msgObj.getClass().getName());
            return;
        }

        pushMsg(msgId,eventData.getData());

    }


    public void pushMsg(int msgId , Object msgObj){


        AbstractMsgCodec<Object> msgCodec = (AbstractMsgCodec<Object>) getGameContext().getMsgCodec(msgId);
        if (msgCodec == null) {
            Logs.CONSUMER.error(" protoParser not found ,id : {} ", msgId);
            return;
        }

        try {

            byte[] msgBytes = msgCodec.encode(msgObj);

            byte[] pushMsgBytes = new byte[4 + msgBytes.length];
            pushMsgBytes[0] = (byte) ((msgId>> 24) & 0xff);
            pushMsgBytes[1] = (byte) ((msgId>> 16) & 0xff);
            pushMsgBytes[2] = (byte) ((msgId>> 8) & 0xff);
            pushMsgBytes[3] = (byte) (msgId & 0xff);

            if(msgBytes.length > 0){
                System.arraycopy(msgBytes, 0, pushMsgBytes, 4,
                        msgBytes.length);
            }

            pushMsg(pushMsgBytes);

        } catch (Exception e) {
            Logs.DEFAULT_LOGGER.error("mqPushConsumer {} pushMsg {} make exception : {} " ,getId() ,msgId, ExceptionUtils.getStackTrace(e));
            return;
        }

    }

    protected abstract void pushMsg(byte[] bytes) throws Exception;

}

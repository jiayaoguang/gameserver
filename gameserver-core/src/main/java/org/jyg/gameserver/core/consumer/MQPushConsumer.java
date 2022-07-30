package org.jyg.gameserver.core.consumer;

import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.consumer.MpscQueueConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.ExecTimeUtil;
import org.jyg.gameserver.core.util.Logs;

public abstract class MQPushConsumer extends MpscQueueConsumer {



    public MQPushConsumer() {

    }



    public void doStart(){
        super.doStart();

    }



    @Override
    protected void processDefaultEvent(int eventId , EventData eventData) {

        int msgId = 0;

        Object msgObj = eventData.getData();
        if(msgObj instanceof ByteMsgObj){
            msgId = getContext().getMsgIdByByteMsgObj((Class<? extends ByteMsgObj>) msgObj.getClass());
        }else if(msgObj instanceof MessageLite){
            msgId = getContext().getMsgIdByProtoClass((Class<? extends MessageLite>) msgObj.getClass());
        }else {
            Logs.DEFAULT_LOGGER.error("msgObj {} not found msgId",msgObj.getClass().getName());
            return;
        }

        pushMsg(msgId,eventData.getData());

    }


    public void pushMsg(int msgId , Object msgObj){


        AbstractMsgCodec<Object> msgCodec = (AbstractMsgCodec<Object>) getContext().getMsgCodec(msgId);
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
            e.printStackTrace();
            return;
        }

    }

    protected abstract void pushMsg(byte[] bytes);

}

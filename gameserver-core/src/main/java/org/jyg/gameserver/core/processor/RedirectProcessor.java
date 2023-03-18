package org.jyg.gameserver.core.processor;

import com.google.protobuf.ByteString;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.EmptyMsgCodec;
import org.jyg.gameserver.core.proto.BytesMsg;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/7/31
 */
@Deprecated
public class RedirectProcessor extends AbstractProcessor<Object> {

    private final int redirectConsumerId;

    public RedirectProcessor(int redirectConsumerId) {
        this.redirectConsumerId = redirectConsumerId;
    }




    @Override
    public void process(Session session, EventData<Object> event) {

//        int msgId = event.getEventId();
//
//        AbstractMsgCodec msgCodec = getContext().getMsgCodec(msgId);
//        if (msgCodec == null) {
//            Logs.CONSUMER.error(" protoParser not found ,id : {} ", msgId);
//            return;
//        }
//
//        byte[] msgBytes;
//
//        try {
//            msgBytes = msgCodec.encode(event.getData());
//
//            BytesMsg.Builder bytesBulder = BytesMsg.newBuilder();
//            bytesBulder.setMsgId(msgId);
//            bytesBulder.setMsgData(ByteString.copyFrom(msgBytes));
//
//            int bytesMsgId = getContext().getMsgIdByProtoClass(BytesMsg.class);
//
//            if(bytesMsgId <= 0){
//                throw new RuntimeException(" bytesMsgId <= 0 ");
//            }
//
//            getContext().getConsumerManager().publicEvent(redirectConsumerId , EventType.REMOTE_MSG_COME,bytesBulder.build() ,bytesMsgId);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

    }
}

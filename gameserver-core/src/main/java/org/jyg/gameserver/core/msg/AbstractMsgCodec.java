package org.jyg.gameserver.core.msg;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jyg.gameserver.core.enums.MsgType;

/**
 * create by jiayaoguang on 2020/10/25
 */
public abstract class AbstractMsgCodec<T> {

    private final int msgId;

    private final MsgType msgType;

    public AbstractMsgCodec(int msgId, MsgType msgType) {
        this.msgId = msgId;
        this.msgType = msgType;
    }

    public int getMsgId() {
        return msgId;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public abstract byte[] encode(T t) throws Exception;

    public abstract T decode(byte[] bytes) throws Exception;

}

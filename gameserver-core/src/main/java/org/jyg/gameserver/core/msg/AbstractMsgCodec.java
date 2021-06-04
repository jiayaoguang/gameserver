package org.jyg.gameserver.core.msg;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jyg.gameserver.core.enums.MsgType;

/**
 * create by jiayaoguang on 2020/10/25
 */
public abstract class AbstractMsgCodec<T> {


    private final MsgType msgType;

    public AbstractMsgCodec(MsgType msgType) {
        this.msgType = msgType;
    }


    public MsgType getMsgType() {
        return msgType;
    }

    public abstract byte[] encode(T t) throws Exception;

    public abstract T decode(byte[] bytes) throws Exception;

}

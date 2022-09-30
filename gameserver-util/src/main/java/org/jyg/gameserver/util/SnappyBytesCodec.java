package org.jyg.gameserver.util;

import org.jyg.gameserver.core.msg.AbstractByteMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.msg.encrypt.MsgEncryptor;

import java.io.IOException;

public class SnappyBytesCodec<T extends ByteMsgObj> extends AbstractByteMsgCodec<T> {


    private final AbstractByteMsgCodec<T> codec;
    private final SnappyMsgEncryptor snappyMsgEncryptor;

    public SnappyBytesCodec(AbstractByteMsgCodec<T> codec) {
        super(codec.getByteMsgClass());
        this.codec = codec;
        this.snappyMsgEncryptor = new SnappyMsgEncryptor();
    }


    @Override
    public byte[] encode(T jsonMsg) throws Exception {


        return snappyMsgEncryptor.encrypt(codec.encode(jsonMsg));
    }

    @Override
    public T decode(byte[] bytes) throws Exception {
        return codec.decode(snappyMsgEncryptor.decrypt(bytes));
    }
}

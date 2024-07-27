package org.jyg.gameserver.core.msg;

import java.io.IOException;

/**
 * create by jiayaoguang on 2020/10/29
 */
public class EmptyMsgCodec extends AbstractByteMsgCodec<ByteMsgObj> {

    private static final byte[] EMPTY_BYTES = new byte[0];

    private final ByteMsgObj instance;


    public EmptyMsgCodec( ByteMsgObj instance) {
        super( instance.getClass());
        this.instance = instance;
    }

    @Override
    public byte[] encode(ByteMsgObj jsonMsg) {
        return EMPTY_BYTES;
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws IOException {
        return instance;
    }

}

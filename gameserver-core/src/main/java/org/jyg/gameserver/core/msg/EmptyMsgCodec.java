package org.jyg.gameserver.core.msg;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

/**
 * create by jiayaoguang on 2020/10/29
 */
public class EmptyMsgCodec extends AbstractByteMsgCodec {

    private final byte[] EMPTY_BYTES = new byte[0];

    private final ByteMsgObj instance;


    public EmptyMsgCodec( ByteMsgObj instance) {
        super( instance.getClass());
        this.instance = instance;
    }

    @Override
    public byte[] encode(ByteMsgObj jsonMsg) throws JsonProcessingException {
        return EMPTY_BYTES;
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws IOException {
        return instance;
    }

}

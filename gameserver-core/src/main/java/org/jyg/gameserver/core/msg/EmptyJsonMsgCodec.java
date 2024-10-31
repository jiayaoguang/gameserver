package org.jyg.gameserver.core.msg;

import org.jyg.gameserver.core.util.Logs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * create by jiayaoguang on 2020/10/29
 */
public class EmptyJsonMsgCodec extends AbstractByteMsgCodec<ByteMsgObj> {

    private static final byte[] EMPTY_JSON_BYTES = "{}".getBytes(StandardCharsets.UTF_8);

    private final ByteMsgObj instance;


    public EmptyJsonMsgCodec(Class<? extends ByteMsgObj> byteMsgObjClazz) {
        super(byteMsgObjClazz);
        try {
            instance = byteMsgObjClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Logs.DEFAULT_LOGGER.error("make exception : " ,e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public byte[] encode(ByteMsgObj jsonMsg) {
        return EMPTY_JSON_BYTES;
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) throws IOException {
        return instance;
    }

}

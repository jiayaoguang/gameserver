package org.jyg.gameserver.core.msg;

import org.jyg.gameserver.core.enums.MsgType;

import java.io.IOException;

/**
 * create by jiayaoguang on 2020/10/25
 */
public abstract class AbstractByteMsgCodec extends AbstractMsgCodec<ByteMsgObj> {

    protected final Class<? extends ByteMsgObj> byteMsgClass;

    public AbstractByteMsgCodec(int msgId, Class<? extends ByteMsgObj> byteMsgClass) {
        super(msgId, MsgType.BYTE_OBJ);
        this.byteMsgClass = byteMsgClass;
    }

    @Override
    public abstract byte[] encode(ByteMsgObj jsonMsg) throws Exception;

    @Override
    public abstract ByteMsgObj decode(byte[] bytes) throws Exception;

    public Class<? extends ByteMsgObj> getByteMsgClass() {
        return byteMsgClass;
    }
}

package org.jyg.gameserver.core.msg;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.enums.MsgType;

/**
 * create by jiayaoguang on 2020/10/25
 */
public abstract class AbstractByteMsgCodec<T> extends AbstractMsgCodec<ByteMsgObj> {

    protected final Class<? extends ByteMsgObj> byteMsgClass;

    public AbstractByteMsgCodec(int msgId, Class<? extends ByteMsgObj> byteMsgClass) {
        super(msgId, MsgType.BYTE_OBJ);
        this.byteMsgClass = byteMsgClass;
    }

    @Override
    public abstract byte[] encode(ByteMsgObj jsonMsg);

    @Override
    public abstract ByteMsgObj decode(byte[] bytes);

    public Class<? extends ByteMsgObj> getByteMsgClass() {
        return byteMsgClass;
    }
}

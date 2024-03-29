package org.jyg.gameserver.core.msg;


/**
 * create by jiayaoguang on 2020/10/25
 */
public abstract class AbstractByteMsgCodec<T extends ByteMsgObj> extends AbstractMsgCodec<T> {

    protected final Class<? extends ByteMsgObj> byteMsgClass;

    public AbstractByteMsgCodec( Class<? extends ByteMsgObj> byteMsgClass) {

        this.byteMsgClass = byteMsgClass;
    }

    @Override
    public abstract byte[] encode(T jsonMsg) throws Exception;

    @Override
    public abstract T decode(byte[] bytes) throws Exception;

    public Class<? extends ByteMsgObj> getByteMsgClass() {
        return byteMsgClass;
    }
}

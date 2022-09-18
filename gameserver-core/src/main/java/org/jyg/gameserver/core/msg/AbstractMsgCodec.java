package org.jyg.gameserver.core.msg;


/**
 * create by jiayaoguang on 2020/10/25
 */
public abstract class AbstractMsgCodec<T> {


    public AbstractMsgCodec() {
    }



    public abstract byte[] encode(T t) throws Exception;

    public abstract T decode(byte[] bytes) throws Exception;

}

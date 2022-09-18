package org.jyg.gameserver.core.msg.encrypt;

import org.jyg.gameserver.core.msg.AbstractMsgCodec;

/**
 * create by jiayaoguang on 2022/9/17
 */
public class EncryptorMsgCodec<T> extends AbstractMsgCodec<T> {

    private final MsgEncryptor msgEncryptor;

    private final AbstractMsgCodec<T> msgCodec;


    public EncryptorMsgCodec( MsgEncryptor msgEncryptor, AbstractMsgCodec<T> msgCodec) {
        this.msgEncryptor = msgEncryptor;
        this.msgCodec = msgCodec;
    }

    @Override
    public byte[] encode(T t) throws Exception {

        return msgEncryptor.encrypt(msgCodec.encode(t));
    }

    @Override
    public T decode(byte[] bytes) throws Exception {
        return msgCodec.decode(msgEncryptor.decrypt(bytes));
    }
}

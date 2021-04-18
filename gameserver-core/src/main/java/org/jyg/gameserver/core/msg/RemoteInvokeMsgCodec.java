package org.jyg.gameserver.core.msg;

import org.jyg.gameserver.core.data.RemoteInvokeData;

/**
 * create by jiayaoguang on 2021/04/17
 */
public class RemoteInvokeMsgCodec extends AbstractByteMsgCodec<ByteMsgObj>{

    public RemoteInvokeMsgCodec(int msgId, Class<? extends RemoteInvokeData> remoteInvokeData) {
        super(msgId, remoteInvokeData);
    }

    @Override
    public byte[] encode(ByteMsgObj jsonMsg) {
        return new byte[0];
    }

    @Override
    public ByteMsgObj decode(byte[] bytes) {
        return null;
    }
}

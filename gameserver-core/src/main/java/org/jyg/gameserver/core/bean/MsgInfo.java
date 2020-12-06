package org.jyg.gameserver.core.bean;

import org.jyg.gameserver.core.msg.AbstractMsgCodec;

/**
 * create by jiayaoguang on 2020/10/24
 */
public class MsgInfo {

    private final int msgId;

    private final AbstractMsgCodec msgCodec;

    public MsgInfo(int msgId, AbstractMsgCodec msgCodec) {
        this.msgId = msgId;
        this.msgCodec = msgCodec;
    }

    public int getMsgId() {
        return msgId;
    }

    public AbstractMsgCodec getMsgCodec() {
        return msgCodec;
    }
}

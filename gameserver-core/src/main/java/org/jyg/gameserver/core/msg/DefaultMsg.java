package org.jyg.gameserver.core.msg;

/**
 * create by jiayaoguang on 2022/8/20
 */
public class DefaultMsg implements ByteMsgObj{

    private int msgId;

    private byte[] msgData;


    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public byte[] getMsgData() {
        return msgData;
    }

    public void setMsgData(byte[] msgData) {
        this.msgData = msgData;
    }
}

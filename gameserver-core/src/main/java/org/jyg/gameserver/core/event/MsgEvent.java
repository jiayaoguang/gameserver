package org.jyg.gameserver.core.event;

public abstract class MsgEvent<T> extends Event{

    private final int msgId;

    private final T msgData;

//    private final int fromConsumerId;


    public MsgEvent(int msgId, T data) {
        this.msgId = msgId;
        this.msgData = data;

    }

    public int getMsgId() {
        return msgId;
    }

    public T getMsgData() {
        return msgData;
    }


    @Override
    public String toString(){
        return "MsgEvent msgId : " + msgId + " ,msgType : " + (msgData == null?"null":msgData.getClass().getSimpleName());
    }

}

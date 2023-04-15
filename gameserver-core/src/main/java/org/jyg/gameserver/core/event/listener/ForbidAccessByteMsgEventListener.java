package org.jyg.gameserver.core.event.listener;


import org.jyg.gameserver.core.event.ForbidAccessMsgEvent;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2023/4/15
 */
public class ForbidAccessByteMsgEventListener implements GameEventListener<ForbidAccessMsgEvent> {

    private final ByteMsgObj forbidHintByteMsgData;

    public ForbidAccessByteMsgEventListener(ByteMsgObj forbidHintByteMsgData) {
        this.forbidHintByteMsgData = forbidHintByteMsgData;
    }

    @Override
    public void onEvent(ForbidAccessMsgEvent forbidAccessProcessorEvent) {
        forbidAccessProcessorEvent.getSession().writeMessage(forbidHintByteMsgData);
    }
}

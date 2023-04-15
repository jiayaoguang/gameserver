package org.jyg.gameserver.core.event.listener;


import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.event.ForbidAccessMsgEvent;

/**
 * create by jiayaoguang on 2023/4/15
 */
public class ForbidAccessProtoEventListener implements GameEventListener<ForbidAccessMsgEvent> {

    private final MessageLite forbidHintProtoData;

    public ForbidAccessProtoEventListener(MessageLite forbidHintProtoData) {
        this.forbidHintProtoData = forbidHintProtoData;
    }

    @Override
    public void onEvent(ForbidAccessMsgEvent forbidAccessProcessorEvent) {
        forbidAccessProcessorEvent.getSession().writeMessage(forbidHintProtoData);
    }
}

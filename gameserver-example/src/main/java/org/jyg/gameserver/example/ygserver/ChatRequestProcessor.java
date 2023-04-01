package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.ChatReplyJson;
import org.jyg.gameserver.example.ygserver.msg.ChatRequestJson;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class ChatRequestProcessor extends ByteMsgObjProcessor<ChatRequestJson> {

    public ChatRequestProcessor() {
        super(ChatRequestJson.class);
    }

    @Override
    public void process(Session session, MsgEvent<ChatRequestJson> eventData) {

        PlayerDB playerDB = session.getSessionObject();

        ChatReplyJson chatReplyJson = new ChatReplyJson();
        chatReplyJson.setContent(eventData.getMsgData().getContent());
        chatReplyJson.setPlayerId(playerDB.getId());
        chatReplyJson.setSendPlayerName(playerDB.getName());

        for(Session otherSession : getGameConsumer().getChannelManager().getSessions()){
            otherSession.writeMessage(chatReplyJson);
        }


    }
}

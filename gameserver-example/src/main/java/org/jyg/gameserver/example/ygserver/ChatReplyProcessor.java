package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.ChatReplyJson;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class ChatReplyProcessor extends ByteMsgObjProcessor<ChatReplyJson> {

    public ChatReplyProcessor() {
        super(ChatReplyJson.class);
    }

    @Override
    public void process(Session session, EventData<ChatReplyJson> eventData) {

        PlayerDB playerDB = session.getSessionObject();

        ChatReplyJson chatReplyJson = eventData.getData();

        System.out.println(chatReplyJson.getSendPlayerName() + ":" + chatReplyJson.getContent());

    }
}

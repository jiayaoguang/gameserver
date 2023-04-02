package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.MQMsgEvent;

public class MQMsgEventListener implements GameEventListener<MQMsgEvent> {
    private final GameConsumer gameConsumer;

    public MQMsgEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(MQMsgEvent event) {

        gameConsumer.processEventMsg(event.getMqSession(), event);

    }
}

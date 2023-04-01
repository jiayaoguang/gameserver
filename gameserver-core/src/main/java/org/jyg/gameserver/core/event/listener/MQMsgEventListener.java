package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.GameEventListener;
import org.jyg.gameserver.core.event.MQMsgEvent;
import org.jyg.gameserver.core.event.NormalMsgEvent;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

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

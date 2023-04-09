package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.session.Session;

public class DisconnectEvent extends SessionEvent {
    public DisconnectEvent(GameConsumer gameConsumer, Session session) {
        super(gameConsumer, session);
    }

}

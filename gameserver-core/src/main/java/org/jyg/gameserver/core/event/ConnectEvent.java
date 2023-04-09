package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.session.Session;

public class ConnectEvent extends SessionEvent{

    public ConnectEvent(GameConsumer gameConsumer,Session session) {
        super(gameConsumer, session);
    }


}

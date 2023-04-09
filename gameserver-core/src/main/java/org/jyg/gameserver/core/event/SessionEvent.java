package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.session.Session;

public abstract class SessionEvent extends Event {

    private final GameConsumer gameConsumer;

    private final Session session;


    public SessionEvent(GameConsumer gameConsumer, Session session) {
        this.gameConsumer = gameConsumer;
        this.session = session;
    }


    public Session getSession() {
        return session;
    }

    public GameConsumer getGameConsumer() {
        return gameConsumer;
    }
}

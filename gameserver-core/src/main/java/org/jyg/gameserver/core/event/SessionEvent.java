package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public abstract class SessionEvent extends Event {

    private final Session session;


    public SessionEvent(Session session) {
        this.session = session;
    }


    public Session getSession() {
        return session;
    }
}

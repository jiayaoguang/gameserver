package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public class SessionEvent<B> extends AbstractEvent<Session, B> {


    public SessionEvent(Event<Session, B> eventLogic) {
        super(eventLogic);
    }
}

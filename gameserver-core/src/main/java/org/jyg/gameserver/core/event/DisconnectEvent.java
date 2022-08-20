package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public class DisconnectEvent extends SessionEvent<Object> {

    public DisconnectEvent(Event<Session, Object> eventLogic) {
        super(eventLogic);
    }

}

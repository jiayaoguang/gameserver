package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public class DisconnectEvent extends SessionEvent {
    public DisconnectEvent(Session session) {
        super(session);
    }

}

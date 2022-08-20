package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public class ConnectEvent extends SessionEvent<Object>{

    public ConnectEvent(Event<Session, Object> event) {
        super(event);
    }

}

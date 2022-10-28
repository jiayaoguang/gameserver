package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public class ConnectEvent extends SessionEvent{

    public ConnectEvent(Session session) {
        super(session);
    }


}

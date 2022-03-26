package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.session.Session;

public class ConnectEvent<B> implements SessionEvent<B>{

    Event<Session ,B> event;

    @Override
    public void onEvent(Session param1, B param2) {
        event.onEvent(param1,param2);
    }
}

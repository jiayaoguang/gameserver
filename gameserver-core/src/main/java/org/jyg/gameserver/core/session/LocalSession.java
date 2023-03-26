package org.jyg.gameserver.core.session;

import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.GameContext;

public class LocalSession extends Session {

    private final GameContext gameContext;
    private final int fromConsumerId;

    public LocalSession(int fromConsumerId, GameContext gameContext) {
        super(0L);
        this.gameContext = gameContext;
        this.fromConsumerId = fromConsumerId;
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void stop() {
        // do nothing
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException();
    }


    @Override
    protected void writeObjMessage(Object msgObj) {
        gameContext.getConsumerManager().publicEvent(fromConsumerId, EventType.DEFAULT_EVENT ,msgObj , 0);
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }
}

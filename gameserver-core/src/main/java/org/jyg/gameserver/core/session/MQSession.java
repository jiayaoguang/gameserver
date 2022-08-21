package org.jyg.gameserver.core.session;

import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.GameContext;

public class MQSession extends Session {

    private final GameContext gameContext;
    private final int mqPushConsumerId;

    public MQSession( int mqPushConsumerId, GameContext gameContext) {
        super(0L);
        this.gameContext = gameContext;
        this.mqPushConsumerId = mqPushConsumerId;
    }

    @Override
    public void start() {

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
        gameContext.getConsumerManager().publicEvent(mqPushConsumerId , EventType.DEFAULT_EVENT ,msgObj , 0);
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }
}

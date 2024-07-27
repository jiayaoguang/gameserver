package org.jyg.gameserver.core.session;

import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
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
        gameContext.getConsumerManager().publishEvent(mqPushConsumerId , new ConsumerDefaultEvent(0, msgObj));
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }
}

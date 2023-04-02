package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;

public class ConsumerDefaultEventListener implements GameEventListener<ConsumerDefaultEvent> {
    private final GameConsumer gameConsumer;

    public ConsumerDefaultEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(ConsumerDefaultEvent consumerOverrideEvent) {

        gameConsumer.processDefaultEvent(consumerOverrideEvent);

    }
}
package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;

public class ConsumerThreadStartEvent extends Event{

    private final GameConsumer gameConsumer;

    public ConsumerThreadStartEvent( GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    public GameConsumer getGameConsumer() {
        return gameConsumer;
    }
}

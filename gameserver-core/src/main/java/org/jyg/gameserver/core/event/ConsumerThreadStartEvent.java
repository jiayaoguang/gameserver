package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.GameConsumer;

public class ConsumerThreadStartEvent implements Event<GameConsumer,Object>{

    private final Event<GameConsumer, Object> event;

    public ConsumerThreadStartEvent(Event<GameConsumer, Object> event) {
        this.event = event;
    }

    @Override
    public void onEvent(GameConsumer param1, Object param2) {

        event.onEvent(param1,param2);

    }
}

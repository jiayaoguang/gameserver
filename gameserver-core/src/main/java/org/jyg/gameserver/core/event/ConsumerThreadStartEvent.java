package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.consumer.Consumer;

public class ConsumerThreadStartEvent implements Event<Consumer,Object>{

    private final Event<Consumer, Object> event;

    public ConsumerThreadStartEvent(Event<Consumer, Object> event) {
        this.event = event;
    }

    @Override
    public void onEvent(Consumer param1, Object param2) {

        event.onEvent(param1,param2);

    }
}

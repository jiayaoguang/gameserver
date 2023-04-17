package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ResultReturnEvent;

public class ResultReturnEventListener implements GameEventListener<ResultReturnEvent> {
    private final GameConsumer gameConsumer;

    public ResultReturnEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(ResultReturnEvent resultReturnEvent) {

        gameConsumer.getResultHandlerManager().onCallBack(resultReturnEvent.getReturnToConsumerRequestId(),resultReturnEvent.getEventId(),resultReturnEvent.getData());

    }
}

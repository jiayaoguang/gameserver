package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ExecutableEvent;

/**
 * create by jiayaoguang on 2020/4/25
 */
public class AsynEventAndCallBackRunnable implements Runnable{

    private final AsynCallEvent asynCallEvent;
    private final CallBackEvent callBackEvent;
    private final GameConsumer gameConsumer;

    public AsynEventAndCallBackRunnable(AsynCallEvent asynCallEvent, CallBackEvent callBackEvent, GameConsumer gameConsumer) {
        this.asynCallEvent = asynCallEvent;
        this.callBackEvent = callBackEvent;
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void run() {
        try {
            Object data = asynCallEvent.execute();
            callBackEvent.setData(data);
            callBackEvent.setSuccess(true);
        } catch (Exception e) {
            Logs.DEFAULT_LOGGER.error("make exception : " ,e);
            callBackEvent.setSuccess(false);
        }
        gameConsumer.getGameContext().getConsumerManager().publishEvent(gameConsumer.getId(), new ExecutableEvent(callBackEvent::execte));

    }

}

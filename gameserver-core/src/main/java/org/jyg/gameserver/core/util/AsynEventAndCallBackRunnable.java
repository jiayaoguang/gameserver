package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.enums.EventType;

/**
 * create by jiayaoguang on 2020/4/25
 */
public class AsynEventAndCallBackRunnable implements Runnable{

    private final AsynCallEvent asynCallEvent;
    private final CallBackEvent callBackEvent;
    private final Consumer consumer;

    public AsynEventAndCallBackRunnable(AsynCallEvent asynCallEvent, CallBackEvent callBackEvent, Consumer consumer) {
        this.asynCallEvent = asynCallEvent;
        this.callBackEvent = callBackEvent;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try {
            Object data = asynCallEvent.execute();
            if (callBackEvent != null) {
                callBackEvent.setData(data);
                consumer.publicEvent(EventType.INNER_MSG, callBackEvent, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

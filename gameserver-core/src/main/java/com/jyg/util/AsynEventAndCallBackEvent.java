package com.jyg.util;

import com.jyg.enums.EventType;

/**
 * create by jiayaoguang on 2020/4/25
 */
public class AsynEventAndCallBackEvent implements Runnable{

    private final Runnable asynEventRunnable;
    private final CallBackEvent callBackEvent;

    public AsynEventAndCallBackEvent(Runnable asynEventRunnable, CallBackEvent callBackEvent) {
        this.asynEventRunnable = asynEventRunnable;
        this.callBackEvent = callBackEvent;
    }

    @Override
    public void run() {
        asynEventRunnable.run();
        if (callBackEvent != null) {
            GlobalQueue.publicEvent(EventType.INNER_MSG, asynEventRunnable, null);
        }
    }

}

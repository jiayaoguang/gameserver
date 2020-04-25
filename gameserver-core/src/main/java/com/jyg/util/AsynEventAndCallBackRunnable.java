package com.jyg.util;

import com.jyg.enums.EventType;

import java.util.concurrent.Callable;

/**
 * create by jiayaoguang on 2020/4/25
 */
public class AsynEventAndCallBackRunnable implements Runnable{

    private final AsynCallEvent asynCallEvent;
    private final CallBackEvent callBackEvent;

    public AsynEventAndCallBackRunnable(AsynCallEvent asynCallEvent, CallBackEvent callBackEvent) {
        this.asynCallEvent = asynCallEvent;
        this.callBackEvent = callBackEvent;
    }

    @Override
    public void run() {
        try {
            Object data = asynCallEvent.execute();
            if (callBackEvent != null) {
                callBackEvent.setData(data);
                GlobalQueue.publicEvent(EventType.INNER_MSG, callBackEvent, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

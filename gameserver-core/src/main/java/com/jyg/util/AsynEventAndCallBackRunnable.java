package com.jyg.util;

import com.jyg.enums.EventType;

/**
 * create by jiayaoguang on 2020/4/25
 */
public class AsynEventAndCallBackRunnable implements Runnable{

    private final AsynCallEvent asynCallEvent;
    private final CallBackEvent callBackEvent;
    private final IGlobalQueue globalQueue;

    public AsynEventAndCallBackRunnable(AsynCallEvent asynCallEvent, CallBackEvent callBackEvent, IGlobalQueue globalQueue) {
        this.asynCallEvent = asynCallEvent;
        this.callBackEvent = callBackEvent;
        this.globalQueue = globalQueue;
    }

    @Override
    public void run() {
        try {
            Object data = asynCallEvent.execute();
            if (callBackEvent != null) {
                callBackEvent.setData(data);
                globalQueue.publicEvent(EventType.INNER_MSG, callBackEvent, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.manager.ResultHandlerManager;
import org.jyg.gameserver.core.timer.Timer;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ResultHandlerTimeOutTimer extends Timer implements ResultHandler {
    private int requestId;

    private final ResultHandler resultHandler;

    private boolean isCall;

    private final ResultHandlerManager resultHandlerManager;

    public ResultHandlerTimeOutTimer(long delayTimeMills, ResultHandler resultHandler, ResultHandlerManager resultHandlerManager, int requestId) {
        super(1, delayTimeMills, delayTimeMills);
        this.resultHandler = resultHandler;
        this.resultHandlerManager = resultHandlerManager;
        this.requestId = requestId;
    }

    @Override
    protected void onTime() {
        if(isCall){
            return;
        }
        this.onTimeout();
    }


    @Override
    public void call(int eventId ,  Object data ){
        if(isCall){
            return;
        }

        isCall = true;
        try{
            resultHandler.call(eventId , data);
        }finally {
            resultHandlerManager.removeCallBackOutTimeTimer(requestId);
            this.cancel();
        }

    }

    @Override
    public void onTimeout() {
        try{
            resultHandler.onTimeout();
        }finally {
            resultHandlerManager.removeCallBackOutTimeTimer(requestId);
        }
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
    //    public void call(){
//        CallBackOutTimeTimer callBackOutTimeTimer = callBackManager.removeCallBackOutTimeTimer(requestId);
//        if(callBackOutTimeTimer == null){
//            return;
//        }
//
//        try{
//            callBackOutTimeTimer.getCallBack().call();
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            callBackOutTimeTimer.getCallBack();
//        }
//    }
}

package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.consumer.ResultHandlerTimeOutTimer;

import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ResultHandlerManager {


    private final Map<Integer , ResultHandlerTimeOutTimer> waitCallBackTimerMap = new HashMap<>();


    public void putCallBackOutTimeTimer(ResultHandlerTimeOutTimer callBackTimeOutTimer){
        int requestId = callBackTimeOutTimer.getRequestId();
        if(waitCallBackTimerMap.containsKey(requestId)){
            throw new IllegalArgumentException("containsKey(requestId)");
        }
        waitCallBackTimerMap.put(requestId , callBackTimeOutTimer);

    }

    public ResultHandlerTimeOutTimer removeCallBackOutTimeTimer(int requestId){
        return waitCallBackTimerMap.remove(requestId);
    }

    public void onCallBack(int requestId , int eventId , Object data ){
        ResultHandlerTimeOutTimer callBackTimeOutTimer = removeCallBackOutTimeTimer(requestId);
        if(callBackTimeOutTimer == null){
            return;
        }

        try{
            callBackTimeOutTimer.call(eventId , data);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            callBackTimeOutTimer.cancel();
        }


    }

}

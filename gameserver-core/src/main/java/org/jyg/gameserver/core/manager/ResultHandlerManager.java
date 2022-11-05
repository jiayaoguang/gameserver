package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.consumer.ResultHandlerTimeOutTimer;

import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ResultHandlerManager {


    private final Map<Long , ResultHandlerTimeOutTimer> waitCallBackTimerMap = new HashMap<>();


    public void putCallBackOutTimeTimer(ResultHandlerTimeOutTimer callBackTimeOutTimer){
        long requestId = callBackTimeOutTimer.getRequestId();
        if(waitCallBackTimerMap.containsKey(requestId)){
            throw new IllegalArgumentException("containsKey(requestId)");
        }
        waitCallBackTimerMap.put(requestId , callBackTimeOutTimer);

    }

    public ResultHandlerTimeOutTimer removeCallBackOutTimeTimer(long requestId){
        return waitCallBackTimerMap.remove(requestId);
    }

    public void onCallBack(long requestId , int eventId , Object data ){
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

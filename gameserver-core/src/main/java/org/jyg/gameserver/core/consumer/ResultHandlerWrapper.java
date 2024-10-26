package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ResultHandlerWrapper<T> implements ResultHandler<T> {

    private ResultHandler resultHandler;

    public ResultHandlerWrapper() {
        this.resultHandler = IgnoreResultHandler.IGNORE_RESULT_HANDLER;
    }

    public ResultHandlerWrapper(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }


    public ResultHandler getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    @Override
    public void call(int eventId , T data){
        resultHandler.call(eventId,data);
    }

    @Override
    public void onTimeout(){
        resultHandler.onTimeout();
    }

}

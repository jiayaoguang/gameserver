package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.consumer.ResultHandlerWrapper;

/**
 * create by jiayaoguang on 2024/10/26
 */
public class InvokeRemoteResultFuture<T> {


    private Object result;

    private final ResultHandlerWrapper resultHandlerWrapper = new ResultHandlerWrapper();


    public ResultHandler getResultHandlerWrapper() {
        return resultHandlerWrapper;
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandlerWrapper.setResultHandler(resultHandler);
    }

    public T getResult(){
        return (T)result;
    }


    public void setResult(Object result){
        this.result = result;
    }

}

package org.jyg.gameserver.core.util;

/**
 * create by jiayaoguang on 2020/4/25
 */
public abstract class CallBackEvent {

    private boolean isSuccess;
    private Object data;

    public abstract void execte();

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

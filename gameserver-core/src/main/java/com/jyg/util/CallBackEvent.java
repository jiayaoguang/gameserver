package com.jyg.util;

import com.jyg.bean.LogicEvent;

/**
 * create by jiayaoguang on 2020/4/25
 */
public abstract class CallBackEvent {

    private boolean isSuccess;
    private Object data;

    public abstract void execte(Object data);

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

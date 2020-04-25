package com.jyg.util;

import com.jyg.bean.LogicEvent;

/**
 * create by jiayaoguang on 2020/4/25
 */
public abstract class CallBackEvent<T> extends LogicEvent<T> {
    public abstract void execte();
}

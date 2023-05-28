package org.jyg.gameserver.core.data;

import java.lang.reflect.Method;

/**
 * create by jiayaoguang on 2023/5/28
 */
public class InvokeMethodInfo {

    private final Method method;
    private final Object instance;

    public InvokeMethodInfo(Method method, Object instance) {
        this.method = method;
        this.instance = instance;
    }


    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }
}

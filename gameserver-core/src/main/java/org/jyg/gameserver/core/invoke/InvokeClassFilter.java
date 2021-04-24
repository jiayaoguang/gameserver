package org.jyg.gameserver.core.invoke;

import cn.hutool.core.lang.Filter;

/**
 * create by jiayaoguang on 2021/4/18
 */
public class InvokeClassFilter implements Filter<Class<?>> {
    @Override
    public boolean accept(Class<?> clazz) {

        for (Class<?> interClazz : clazz.getInterfaces()) {
            if(interClazz == IRemoteInvoke.class){
                return true;
            }
        }

        return false;
    }
}

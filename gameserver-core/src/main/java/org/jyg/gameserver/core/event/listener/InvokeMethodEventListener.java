package org.jyg.gameserver.core.event.listener;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.InvokeMethodInfo;
import org.jyg.gameserver.core.event.InvokeMethodEvent;
import org.jyg.gameserver.core.util.Logs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokeMethodEventListener implements GameEventListener<InvokeMethodEvent> {
    private final GameConsumer gameConsumer;

    public InvokeMethodEventListener(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(InvokeMethodEvent invokeMethodEvent) {


        InvokeMethodInfo invokeMethodInfo = gameConsumer.getRemoteMethodInvokeManager().getInvokeMethodInfo(invokeMethodEvent.getMethodUniqueName());

        if (invokeMethodInfo == null) {
            Logs.DEFAULT_LOGGER.error("MethodUniqueName < {} > not found : " , invokeMethodEvent.getMethodUniqueName());
            return;
        }


        try {
            Object returnObj = invokeMethodInfo.getMethod().invoke(invokeMethodInfo.getInstance(), invokeMethodEvent.getMethodParams());

            if(invokeMethodInfo.getMethod().getReturnType() != Void.class && invokeMethodEvent.getRequestId() > 0){
                gameConsumer.eventReturn(invokeMethodEvent.getFromConsumerId() , returnObj , invokeMethodEvent.getRequestId());
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            Logs.DEFAULT_LOGGER.error("invokeMethod {} make exception {}" , invokeMethodEvent.getMethodUniqueName(), ExceptionUtils.getStackTrace(e));
        }

    }


}
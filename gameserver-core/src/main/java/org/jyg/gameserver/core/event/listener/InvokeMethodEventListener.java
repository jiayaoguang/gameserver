package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.InvokeMethodInfo;
import org.jyg.gameserver.core.event.InvokeMethodEvent;

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
            throw new IllegalArgumentException("MethodUniqueName not found : " + invokeMethodEvent.getMethodUniqueName());
        }


        try {
            Object returnObj = invokeMethodInfo.getMethod().invoke(invokeMethodInfo.getInstance(), invokeMethodEvent.getMethodParams());

            if(invokeMethodInfo.getMethod().getReturnType() != Void.class && invokeMethodEvent.getRequestId() > 0){
                gameConsumer.eventReturn(invokeMethodEvent.getFromConsumerId() , returnObj , invokeMethodEvent.getRequestId());
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }


}
package org.jyg.gameserver.core.manager;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.annotaion.CallRemoteMethod;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
import org.jyg.gameserver.core.util.Logs;

import java.lang.reflect.Method;

/**
 * create by jiayaoguang on 2024/9/29
 * 远程调用，调用方的代理类
 */
public class InvokeRemoteProxy {


    private final RemoteMethodInvokeManager remoteMethodInvokeManager;


    public InvokeRemoteProxy(RemoteMethodInvokeManager remoteMethodInvokeManager) {
        this.remoteMethodInvokeManager = remoteMethodInvokeManager;
    }

    /**
     * param @This Object obj,//目标对象 -- 注入被拦截的目标对象
     * @param arguments arguments
     * @param method method
     * param  @SuperCall Callable<?> callable
     */
    @RuntimeType
    public Object intercept( @AllArguments Object[] arguments, @Origin Method method) throws Exception {

        Logs.DEFAULT_LOGGER.debug("intercept method .................... : {}" , method.getName());


        CallRemoteMethod callRemoteMethod = method.getAnnotation(CallRemoteMethod.class);
        if(callRemoteMethod == null){
            Logs.DEFAULT_LOGGER.debug("CallRemoteMethod not exist .................... : {}" , method.getName());
            return null;
        }

        int targetConsumerId = callRemoteMethod.targetConsumerId();

        String methodUname = callRemoteMethod.uname();

        if(StringUtils.isEmpty(methodUname)){
            Logs.DEFAULT_LOGGER.debug("CallRemoteMethod methodUname null .................... : {}" , method.getName());
            return null;
        }

        if (method.getReturnType() == Void.class) {
            remoteMethodInvokeManager.invokeRemoteMethod(targetConsumerId, methodUname, arguments);
            return null;
        } else {

            try {
                return remoteMethodInvokeManager.invokeRemoteMethodAndWait(targetConsumerId, methodUname, arguments);
            } catch (RequestTimeoutException e) {
                throw new RuntimeException(e);
            }

        }

    }



}

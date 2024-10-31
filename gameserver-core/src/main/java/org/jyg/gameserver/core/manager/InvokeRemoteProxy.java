package org.jyg.gameserver.core.manager;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.annotaion.InvokeRemoteMethod;
import org.jyg.gameserver.core.util.Logs;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

//        Logs.DEFAULT_LOGGER.debug("intercept method .................... : {}" , method.getName());


        InvokeRemoteMethod invokeRemoteMethod  = method.getAnnotation(InvokeRemoteMethod.class);
        if(invokeRemoteMethod == null){
            Logs.DEFAULT_LOGGER.error("invoke RemoteMethod not exist .................... : {}" , method.getName());
            return null;
        }


        int targetConsumerId = invokeRemoteMethod.targetConsumerId();

        String methodUname = invokeRemoteMethod.uname();

        if(StringUtils.isEmpty(methodUname)){
            Logs.DEFAULT_LOGGER.error("invoke RemoteMethod methodUname null .................... : {}" , method.getName());
            return null;
        }

        if (method.getReturnType() == Void.class) {
            remoteMethodInvokeManager.invokeRemoteMethod(targetConsumerId, methodUname, arguments);
            return null;
        }else if(method.getReturnType() == InvokeRemoteResultFuture.class){
            /*
             * 如果方法返回类型是InvokeRemoteResultFuture
             * 收到远程处理后的结果后会调用InvokeRemoteResultFuture的ResultHandler
             * 需要给返回值设置获取结果后的处理逻辑
             */
            InvokeRemoteResultFuture invokeRemoteResultFuture = new InvokeRemoteResultFuture();
            remoteMethodInvokeManager.invokeRemoteMethod(targetConsumerId, methodUname, arguments , invokeRemoteResultFuture.getResultHandlerWrapper());
            return invokeRemoteResultFuture;
        } else {

            return remoteMethodInvokeManager.invokeRemoteMethodAndWait(targetConsumerId, methodUname, arguments);

        }

    }



}

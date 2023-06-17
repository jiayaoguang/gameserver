package org.jyg.gameserver.test.invoke;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.exception.RequestTimeoutException;
import org.jyg.gameserver.core.manager.RemoteMethodInvokeManager;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.db.BaseDBEntity;
import org.jyg.gameserver.db.ConsumerDBManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class InvokeProxyManager {

    private final GameConsumer gameConsumer;

    ByteBuddy byteBuddy;

    RemoteMethodInvokeManager remoteMethodInvokeManager;



    public InvokeProxyManager(GameConsumer gameConsumer ) {
        this.gameConsumer = gameConsumer;
        this.byteBuddy = new ByteBuddy();
        this.remoteMethodInvokeManager = gameConsumer.getRemoteMethodInvokeManager();
    }


    public <T> T createProxy(Class<T> tClazz ,int targetConsumerId) throws IllegalAccessException, InstantiationException {
        T t = byteBuddy.subclass(tClazz)
                .name("org.jyg.invoke.bytebuddy." + tClazz.getSimpleName())
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(MethodProxyAdvisor.class))
                .defineField("invokeManager" , RemoteMethodInvokeManager.class)
                .defineField("targetConsumerId" , Integer.class)
//                .defineField("resultHandler" , ResultHandler.class)
//                .defineConstructor()
//                .intercept(MethodDelegation.to(Target2.class))
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded()
                .newInstance();



        try {
            {
                Field field = t.getClass().getDeclaredField("invokeManager");
                field.setAccessible(true);
                field.set(t,remoteMethodInvokeManager);
            }

            {
                Field field = t.getClass().getDeclaredField("targetConsumerId");
                field.setAccessible(true);
                field.set(t,targetConsumerId);
            }

//            {
//                Field field = t.getClass().getDeclaredField("resultHandler");
//                field.setAccessible(true);
//                field.set(t,resultHandler);
//            }


        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return t;
    }

    public static class MethodProxyAdvisor {


        @RuntimeType
        public static Object intercept(@This Object obj,//目标对象 -- 注入被拦截的目标对象
                                       @AllArguments Object[] arguments, @Origin Method method, @SuperCall Callable<?> callable) throws Exception {

            AllUtil.println("intercept method ...................."+ method.getName());
            Field remoteMethodInvokeManagerField = null;
            try {
                remoteMethodInvokeManagerField = obj.getClass().getDeclaredField("invokeManager");
                remoteMethodInvokeManagerField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(e);
            }
            RemoteMethodInvokeManager remoteMethodInvokeManager = null;
            try {
                remoteMethodInvokeManager = (RemoteMethodInvokeManager)remoteMethodInvokeManagerField.get(obj);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }

            if(remoteMethodInvokeManager == null){
                throw new IllegalArgumentException("RemoteMethodInvokeManager == null");
            }


            Field targetConsumerIdField = null;
            try {
                targetConsumerIdField = obj.getClass().getDeclaredField("targetConsumerId");
                targetConsumerIdField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(e);
            }
            int targetConsumerId = 0;
            try {
                targetConsumerId = (Integer) targetConsumerIdField.get(obj);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }

            String methodKey;

            RemoteMethod remoteMethod = method.getAnnotation(RemoteMethod.class);
            if(remoteMethod != null && StringUtils.isNotEmpty(remoteMethod.uname())){
                methodKey = remoteMethod.uname();
            }else {
                methodKey = remoteMethodInvokeManager.createMethodUniqueName(obj.getClass().getSuperclass() , method);
            }

            if(method.getReturnType() == Void.class){
                remoteMethodInvokeManager.invokeRemoteMethod( targetConsumerId , methodKey,arguments);
                return null;
            }else {
                try {
                    return remoteMethodInvokeManager.invokeRemoteMethodAndWait( targetConsumerId , methodKey,arguments);
                } catch (RequestTimeoutException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

}

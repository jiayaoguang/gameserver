package org.jyg.gameserver.core.manager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.*;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.InvokeMethodInfo;
import org.jyg.gameserver.core.event.InvokeMethodEvent;
import org.jyg.gameserver.core.exception.RequestTimeoutException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2023/5/28
 */
public class RemoteMethodInvokeManager implements Lifecycle{


    private InstanceManager instanceManager;

    private ProxyFactoryManager proxyFactoryManager;


    private Map<String, InvokeMethodInfo> invokeMethodMap = new HashMap<>( 256,0.5f);


    private final Map<Class<?>, Object> methodProxyMap = new HashMap<>(256,0.5f);

    private GameConsumer gameConsumer;


    public RemoteMethodInvokeManager(ProxyFactoryManager proxyFactoryManager, GameConsumer gameConsumer) {
        this.proxyFactoryManager = proxyFactoryManager;
        this.gameConsumer = gameConsumer;
        this.instanceManager = gameConsumer.getInstanceManager();
    }

    @Override
    public void start() {
        for( Object instance : instanceManager.getAllInstance() ){
            findClassRemoteMethods(instance);
        }
    }


    private void findClassRemoteMethods(Object remoteMethodInstance){


        Class<?> remoteMethodClazz = remoteMethodInstance.getClass();
        for(Method method : remoteMethodClazz.getMethods()){
            RemoteMethod remoteMethodAnno = method.getAnnotation(RemoteMethod.class);
            if(remoteMethodAnno == null){
                continue;
            }


            if(StringUtils.isNotEmpty(remoteMethodAnno.uname())){
                if(invokeMethodMap.containsKey(remoteMethodAnno.uname())){
                    throw new IllegalArgumentException("duplicate remoteMethod : " + remoteMethodAnno.uname());
                }
                invokeMethodMap.put(remoteMethodAnno.uname(),new InvokeMethodInfo(method ,remoteMethodInstance ));
            }


            String methodName = createMethodUniqueName(remoteMethodClazz,method);
            if(invokeMethodMap.containsKey(methodName)){
                throw new IllegalArgumentException("duplicate remoteMethod : " + methodName);
            }
            invokeMethodMap.put(methodName,new InvokeMethodInfo(method ,remoteMethodInstance ));

        }
    }

    public String createMethodUniqueName(Class<?> remoteMethodClazz, Method method){


        StringBuilder sb = new StringBuilder();
        if(!GameConsumer.class.isAssignableFrom(remoteMethodClazz)){
            sb.append(remoteMethodClazz.getName()).append('#');
        }

        sb.append(method.getName());


        for (Class<?> parameterType : method.getParameterTypes()) {
            sb.append(';').append(parameterType.getName());
        }

        return sb.toString();
    }


    public InvokeMethodInfo getInvokeMethodInfo(String uname){
        return invokeMethodMap.get(uname);
    }


    @Override
    public void stop() {

    }


    public void invokeRemoteMethod(int targetConsumerId , String methodUname  ){
        this.invokeRemoteMethod(targetConsumerId ,0L, methodUname , ArrayUtils.EMPTY_OBJECT_ARRAY);
    }


    public void invokeRemoteMethod(int targetConsumerId , String methodUname , Object... methodParams ){
        this.invokeRemoteMethod(targetConsumerId ,0L, methodUname , methodParams );
    }


    public void invokeRemoteMethod(int targetConsumerId , String methodUname , Object[] methodParams , ResultHandler resultHandler){

        long requestId = 0L;

        if(resultHandler != null){
            requestId = gameConsumer.registerCallBackMethod(resultHandler);
        }

        this.invokeRemoteMethod(targetConsumerId, requestId , methodUname , methodParams );
    }



    private void invokeRemoteMethod(int targetConsumerId , long requestId , String methodUname , Object[] methodParams ){

        EventData eventData = new EventData();
        InvokeMethodEvent invokeMethodEvent = new InvokeMethodEvent();
        invokeMethodEvent.setMethodParams(methodParams);
        invokeMethodEvent.setMethodUniqueName(methodUname);
        invokeMethodEvent.setFromConsumerId(gameConsumer.getId());


        invokeMethodEvent.setRequestId(requestId);


        eventData.setEvent(invokeMethodEvent);

        gameConsumer.getGameContext().getConsumerManager().publishEvent(targetConsumerId, eventData);
    }


    /**
     * 非阻塞同步调用
     */
    public Object invokeRemoteMethodAndWait(int targetConsumerId , String methodUname , Object... methodParams) throws RequestTimeoutException {
        long requestId = gameConsumer.allocateRequestId();
        this.invokeRemoteMethod(targetConsumerId, requestId , methodUname , methodParams );
        return new ConsumerFuture(requestId , (AbstractThreadQueueGameConsumer) gameConsumer).waitForResult();

    }


    /**
     * 创建调用远程方法的代理类，
     * TODO 带返回值的远程方法调用稳定性待测试
     */
    public <T> T getOrCreateRemoteMethodProxy(Class<T> clazz){

        T t = (T) methodProxyMap.get(clazz);
        if (t != null) {
            return t;
        }

        try {
            t = proxyFactoryManager.createProxy(clazz,new InvokeRemoteProxy(this));
            methodProxyMap.put(clazz,t);
            return t;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


}

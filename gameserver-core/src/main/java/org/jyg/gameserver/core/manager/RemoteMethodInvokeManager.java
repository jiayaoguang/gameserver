package org.jyg.gameserver.core.manager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.annotaion.RemoteMethod;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.InvokeMethodInfo;
import org.jyg.gameserver.core.event.InvokeMethodEvent;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang on 2023/5/28
 */
public class RemoteMethodInvokeManager implements Lifecycle{


    private InstanceManager instanceManager;


    private Map<String, InvokeMethodInfo> invokeMethodMap = new HashMap<>( 256,0.5f);

    private GameConsumer gameConsumer;


    public RemoteMethodInvokeManager(GameConsumer gameConsumer) {
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



        for(Method method : remoteMethodInstance.getClass().getMethods()){
            RemoteMethod remoteMethodAnno = method.getAnnotation(RemoteMethod.class);
            if(remoteMethodAnno == null){
                continue;
            }


            if(StringUtils.isNotEmpty(remoteMethodAnno.uname())){
                if(invokeMethodMap.containsKey(remoteMethodAnno.uname())){
                    throw new IllegalArgumentException("dumplicate remoteMethod : " + remoteMethodAnno.uname());
                }
                invokeMethodMap.put(remoteMethodAnno.uname(),new InvokeMethodInfo(method ,remoteMethodInstance ));
            }


            String methodName = createMethodUniqueName(method);
            if(invokeMethodMap.containsKey(methodName)){
                throw new IllegalArgumentException("dumplicate remoteMethod : " + methodName);
            }
            invokeMethodMap.put(methodName,new InvokeMethodInfo(method ,remoteMethodInstance ));

        }
    }

    public String createMethodUniqueName(Method method){
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName());


        for (Class parameterType : method.getParameterTypes()) {
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
        this.invokeRemoteMethod(targetConsumerId , methodUname , ArrayUtils.EMPTY_OBJECT_ARRAY, null);
    }


    public void invokeRemoteMethod(int targetConsumerId , String methodUname , Object[] methodParams ){
        this.invokeRemoteMethod(targetConsumerId , methodUname , methodParams , null);
    }


    public void invokeRemoteMethod(int targetConsumerId , String methodUname , Object[] methodParams , ResultHandler resultHandler){

        EventData eventData = new EventData();
        InvokeMethodEvent invokeMethodEvent = new InvokeMethodEvent();
        invokeMethodEvent.setMethodParams(methodParams);
        invokeMethodEvent.setMethodUniqueName(methodUname);
        invokeMethodEvent.setFromConsumerId(gameConsumer.getId());


        if(resultHandler != null){
            long requestId = gameConsumer.registerCallBackMethod(resultHandler);
            invokeMethodEvent.setRequestId(requestId);
        }


        eventData.setEvent(invokeMethodEvent);

        gameConsumer.getGameContext().getConsumerManager().publicEvent(targetConsumerId, eventData);
    }

}

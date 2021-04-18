package org.jyg.gameserver.core.manager;

import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.StrUtil;
import org.jyg.gameserver.core.anno.InvokeName;
import org.jyg.gameserver.core.util.IRemoteInvoke;
import org.jyg.gameserver.core.util.InvokeClassFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * create by jiayaoguang on 2021/4/18
 */
public class RemoteInvokeManager {

    private final Map<String , Class<? extends IRemoteInvoke>> remoteInvokeClassMap;

    public RemoteInvokeManager() {
        this.remoteInvokeClassMap = new HashMap<>();
    }

    public void init(String packagePath){
        if(packagePath == null){
            packagePath = StrUtil.EMPTY;
        }

        Set<Class<?>> classSet = ClassScanner.scanPackage(packagePath,new InvokeClassFilter());

        for(Class<?> clazz : classSet){

            boolean isInvokeClass = false;

            for (Class<?> interClazz : clazz.getInterfaces()) {
                if (interClazz == IRemoteInvoke.class) {
                    isInvokeClass = true;
                    break;
                }
            }
            if(!isInvokeClass){
                continue;
            }

            @SuppressWarnings("unchecked")
            Class<? extends IRemoteInvoke> invokeClazz = (Class<? extends IRemoteInvoke>)clazz;

            InvokeName invokeNameAnno = invokeClazz.getAnnotation(InvokeName.class);
            final String invokeName ;

            if(invokeNameAnno != null){
                invokeName = invokeNameAnno.name();
            }else {
                invokeName = invokeClazz.getName();
            }

            if(remoteInvokeClassMap.containsKey(invokeName)){
                throw  new RuntimeException("duplicate invokeName" + invokeName);
            }

            remoteInvokeClassMap.put(invokeName , invokeClazz);

        }


    }


    public Class<? extends IRemoteInvoke> getInvokeClass(String invokeName){
        return remoteInvokeClassMap.get(invokeName);
    }

}

package org.jyg.gameserver.core.manager;

import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.anno.InvokeName;
import org.jyg.gameserver.core.invoke.FileClassLoader;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;
import org.jyg.gameserver.core.invoke.InvokeClassFilter;
import org.jyg.gameserver.core.util.Logs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * create by jiayaoguang on 2021/4/18
 */
public class RemoteInvokeManager {

    /**
     * 动态加载 invoke 类的加载器
     */
    private final FileClassLoader invokeClassLoader;

    private final Map<String, IRemoteInvoke> remoteInvokeMap;

    public RemoteInvokeManager() {
        this.remoteInvokeMap = new HashMap<>();

        this.invokeClassLoader = new FileClassLoader();
    }

    public void init(String packagePath) {
        if (packagePath == null) {
            packagePath = StrUtil.EMPTY;
        }

        Set<Class<?>> classSet = ClassScanner.scanPackage(packagePath, new InvokeClassFilter());

        for (Class<?> clazz : classSet) {

            if(clazz.isAnonymousClass() || clazz.isMemberClass()){
                return;
            }

            boolean isInvokeClass = false;

            for (Class<?> interClazz : clazz.getInterfaces()) {
                if (interClazz == IRemoteInvoke.class) {
                    isInvokeClass = true;
                    break;
                }
            }
            if (!isInvokeClass) {
                continue;
            }


            @SuppressWarnings("unchecked")
            Class<? extends IRemoteInvoke> invokeClazz = (Class<? extends IRemoteInvoke>) clazz;

            addRemoteInvoke(invokeClazz);

        }


    }

    private IRemoteInvoke addRemoteInvoke(Class<? extends IRemoteInvoke> invokeClazz) {

        final String invokeClassName = invokeClazz.getName();


        if (remoteInvokeMap.containsKey(invokeClassName)) {
            throw new RuntimeException("duplicate invokeClassName " + invokeClassName);
        }

        try {
            IRemoteInvoke remoteInvoke = invokeClazz.newInstance();

            remoteInvokeMap.put(invokeClassName, remoteInvoke);

            InvokeName invokeNameAnno = invokeClazz.getAnnotation(InvokeName.class);
            if (invokeNameAnno != null) {
                String invokeName = invokeNameAnno.name();
                if (remoteInvokeMap.containsKey(invokeName)) {
                    throw new RuntimeException("duplicate invokeName " + invokeName);
                }

                if(StringUtils.isEmpty(invokeName)){
                    throw new RuntimeException(" invokeName isEmpty " + invokeName);
                }

                if(invokeName.equals("null")){
                    throw new RuntimeException(" invokeName can not is string \"null\" ");
                }

                remoteInvokeMap.put(invokeName, remoteInvoke);

                Logs.DEFAULT_LOGGER.info("add invoke class : {} , invokeName {}", invokeClassName, invokeName);
            }else {
                Logs.DEFAULT_LOGGER.info("add invoke class : {} , not have Annotation invokeName", invokeClassName);
            }



            return remoteInvoke;

        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("InstantiationException");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("IllegalAccessException");
        }

    }


    private void removeRemoteInvoke(Class<? extends IRemoteInvoke> invokeClazz) {

        remoteInvokeMap.remove(invokeClazz.getName());


        InvokeName invokeNameAnno = invokeClazz.getAnnotation(InvokeName.class);
        if (invokeNameAnno != null) {
            String invokeName = invokeNameAnno.name();
            if (remoteInvokeMap.containsKey(invokeName)) {
                throw new RuntimeException("duplicate invokeName " + invokeName);
            }
            remoteInvokeMap.remove(invokeName);
        }

    }


    public IRemoteInvoke getInvokeClass(String invokeName) {

        IRemoteInvoke remoteInvoke = remoteInvokeMap.get(invokeName);
        if (remoteInvoke != null) {
            return remoteInvoke;
        }



        try {
            Class<? extends IRemoteInvoke> invokeClass = (Class<? extends IRemoteInvoke>) invokeClassLoader.loadClass(invokeName);
            remoteInvoke = addRemoteInvoke(invokeClass);
            return remoteInvoke;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Logs.DEFAULT_LOGGER.error(" invokeName {} not found", invokeName);
            return null;
        }

    }

}

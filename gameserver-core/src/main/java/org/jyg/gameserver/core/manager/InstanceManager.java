package org.jyg.gameserver.core.manager;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2021/5/1
 */
public class InstanceManager implements Lifecycle {

    private volatile boolean isStart = false;

    private final Map<Class<?>, Object> instanceMap;


    public InstanceManager() {
        this.instanceMap = new LinkedHashMap<>();
    }


    @Override
    public void start(){
        if(isStart){
            return;
        }

        isStart = true;


        for(Object obj : instanceMap.values() ){

            if(obj instanceof GameContext){
                continue;
            }
            if(obj instanceof GameConsumer){
                continue;
            }

            if(obj instanceof Lifecycle){
                Lifecycle lifecycle = (Lifecycle)obj;
                lifecycle.start();
            }

        }

    }

    @Override
    public void stop(){
        for(Object obj : instanceMap.values() ){

            if(obj instanceof GameContext){
                continue;
            }
            if(obj instanceof GameConsumer){
                continue;
            }

            if(obj instanceof Lifecycle){
                Lifecycle lifecycle = (Lifecycle)obj;
                lifecycle.stop();
            }

        }
    }


    public synchronized void putInstance(Class<?> clazz)  {

        putInstance(clazz, clazz);

    }


    public synchronized void putInstance(Class<?> supperClazz, Class<?> clazz) {


        if(isStart){
            Logs.DEFAULT_LOGGER.error(" server is start put class {} Instance fail" , clazz.getCanonicalName());
            throw new IllegalArgumentException("server is start putInstance fail : " + clazz.getCanonicalName());
        }


        if(getInstance(supperClazz) != null){
            Logs.DEFAULT_LOGGER.error(" already havs this instance putInstance {} fail" , clazz.getCanonicalName());
            throw new IllegalArgumentException("already havs this instance putInstance fail : " + clazz.getCanonicalName());
        }

        Constructor<?>[] constructors = clazz.getConstructors();

        if (constructors.length != 1) {
            throw new IllegalArgumentException(" constructors.length != 1 : " + constructors.length + " type : " + clazz.getCanonicalName());
        }

        Constructor<?> constructor = constructors[0];

        try {
            Object instance = createInstance(constructor);
            putInstance(supperClazz, instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }

    }

    private Object createInstance(Constructor<?> constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        if (parameterTypes.length == 0) {
            return constructor.newInstance();
        }

        Object[] paramObjs = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            Object paramObj = getInstance(paramType);
            if (paramObj == null) {
                throw new InstantiationException(" paramObj not found " + paramType.getCanonicalName());
            }
            paramObjs[i] = paramObj;
        }

        Object instance = constructor.newInstance(paramObjs);
        return instance;
    }

    public synchronized void putInstance(Object instance) {
        this.putInstance(instance.getClass(), instance);
    }

    public synchronized void putInstance( Lifecycle instance) {
        this.putInstance(instance.getClass() , instance);
    }

    public synchronized void putInstance(Class<? extends Lifecycle> clazz, Lifecycle instance) {
        putInstance(clazz , (Object)instance);
    }


    public synchronized void putInstance(Class<?> clazz, Object instance) {


        if(isStart){
            Logs.DEFAULT_LOGGER.error(" server is start putInstance {} fail" , clazz.getCanonicalName());
            throw new IllegalArgumentException("server is start putInstance fail : " + clazz.getCanonicalName());
        }


        if(getInstance(clazz) != null){
            Logs.DEFAULT_LOGGER.error(" already havs this instance putInstance {} fail" , clazz.getCanonicalName());
            throw new IllegalArgumentException("already havs this instance putInstance fail : " + clazz.getCanonicalName());
        }


        instanceMap.put(clazz, instance);

    }


    private synchronized void removeInstance(Class<?> clazz) {
        if(isStart){
            Logs.DEFAULT_LOGGER.error(" server is start putInstance {} fail" , clazz.getCanonicalName());
            throw new IllegalArgumentException("server is start removeInstance fail : " + clazz.getCanonicalName());
        }
        instanceMap.remove(clazz);

    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {

        return (T) instanceMap.get(clazz);
    }


}

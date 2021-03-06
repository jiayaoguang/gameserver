package org.jyg.gameserver.util;

import cn.hutool.core.lang.ClassScanner;
import com.google.inject.*;
import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * create by jiayaoguang on 2020/5/10
 */
public class InjectGameServerBootstrap extends GameServerBootstrap {

    private final List<Class<?>> classList = new ArrayList();
    private Injector injector;

    public InjectGameServerBootstrap() {
        super();
    }

    public void registerClass(Class<?> clazz) {
        if(isStart){
            throw new IllegalArgumentException("registerProtoProcessorClass fail , server is start");
        }
        classList.add(clazz);
    }

    public static void scannerInjectClass(String packageName){
        Set<Class<?>> classSet = ClassScanner.scanPackage(packageName);
        for (Class<?> clazz : classSet) {

            Inject inject = clazz.getAnnotation(Inject.class);
            Constructor[] constructors = clazz.getConstructors();
            for(Constructor constructor :constructors){
                if(constructor.getDeclaredAnnotation(Inject.class) != null){

                    Logs.DEFAULT_LOGGER.info("==============" + constructor.getDeclaredAnnotation(Inject.class) + " ...."+ clazz);
                }
            }
        }
    }

    @Override
    public void beforeStart() {

        Module module = binder -> {
            for(Class<?> clazz : classList){
                binder.bind(clazz).in(Scopes.SINGLETON);
            }
        };

        injector = Guice.createInjector(module);

        for(Class<?> clazz : classList){
            Object obj = injector.getInstance(clazz);
            if(obj instanceof ProtoProcessor){
                ProtoProcessor<? extends MessageLite> protoProcessor = (ProtoProcessor<? extends MessageLite>)obj;
                int msgId = protoProcessor.getProtoMsgId();
                if(msgId == -1){
                    throw new IllegalArgumentException(" getProtoEventId -1 ");
                }
                addProtoProcessor( msgId , protoProcessor);
            }else if(obj instanceof HttpProcessor){
                HttpProcessor httpProcessor = (HttpProcessor)obj;
                String path = httpProcessor.getPath();
                if(path == null){
                    throw new IllegalArgumentException(" getProtoEventId -1 ");
                }
                addHttpProcessor( httpProcessor);
            }
        }
    }

    public Injector getInjector() {
        return injector;
    }

}

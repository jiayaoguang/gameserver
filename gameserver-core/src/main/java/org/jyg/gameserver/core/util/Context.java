package org.jyg.gameserver.core.util;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.bean.ServerConfig;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.manager.*;
import org.jyg.gameserver.core.session.Session;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class Context {

    private static final String DEFAULT_CONFIG_FILE_NAME = "jyg.properties";
    private String configFileName = DEFAULT_CONFIG_FILE_NAME;

    private final Consumer globalQueue;
    private final EventLoopGroupManager eventLoopGroupManager;
//    private final ExecutorManager executorManager;

    private volatile boolean isStart = false;


    private final ServerConfig serverConfig = new ServerConfig();

    private final ConsumerManager consumerManager = new ConsumerManager();

//    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    //TODO I think ...
    private final SingleThreadExecutorManagerPool singleThreadExecutorManagerPool;

    private Object2IntMap<Class<? extends GeneratedMessageV3>> protoClazz2MsgidMap = new Object2IntOpenHashMap<>();
    private Int2ObjectMap<Class<? extends GeneratedMessageV3>> msgId2protoClazzMap = new Int2ObjectOpenHashMap<>();
    private Int2ObjectMap<Parser<? extends GeneratedMessageV3>> msgId2protoParserMap = new Int2ObjectOpenHashMap<>();

    public Context(Consumer globalQueue) {
        this.globalQueue = globalQueue;
        this.eventLoopGroupManager = new EventLoopGroupManager();
//        this.executorManager = new ExecutorManager(10, globalQueue);
        this.singleThreadExecutorManagerPool = new SingleThreadExecutorManagerPool(globalQueue);
    }

    public Consumer getGlobalQueue() {
        return globalQueue;
    }

    public EventLoopGroupManager getEventLoopGroupManager() {
        return eventLoopGroupManager;
    }


    public void addMsgId2ProtoMapping(int msgId, Class<? extends GeneratedMessageV3> protoClazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GeneratedMessageV3 defaultInstance = (GeneratedMessageV3)protoClazz.getMethod("getDefaultInstance").invoke(null);
        addMsgId2ProtoMapping(msgId , defaultInstance);
    }

    public void addMsgId2ProtoMapping(int msgId,  GeneratedMessageV3 defaultInstance) {
        this.protoClazz2MsgidMap.put(defaultInstance.getClass(), msgId);
        this.msgId2protoClazzMap.put(msgId, defaultInstance.getClass());
        this.msgId2protoParserMap.put(msgId , defaultInstance.getParserForType());
    }

    public Parser<? extends GeneratedMessageV3> getProtoParserByMsgId (int msgId){
        return this.msgId2protoParserMap.get(msgId);
    }

    public Class<? extends GeneratedMessageV3> getProtoClassByMsgId(int msgId) {
        return msgId2protoClazzMap.get(msgId);
    }

    public int getMsgIdByProtoClass( Class<? extends GeneratedMessageV3> protoClass) {

        return protoClazz2MsgidMap.getInt(protoClass);
    }


    public ExecutorManager getSingleThreadExecutorManager(Session session) {
        return singleThreadExecutorManagerPool.getSingleThreadExecutorManager(session);
    }

    public synchronized void start() {
        if(isStart){
            AllUtil.println(" already start .... ");
            return;
        }
        this.isStart = true;
        this.protoClazz2MsgidMap = Object2IntMaps.unmodifiable(this.protoClazz2MsgidMap);
        this.msgId2protoClazzMap = Int2ObjectMaps.unmodifiable(this.msgId2protoClazzMap);
        this.msgId2protoParserMap = Int2ObjectMaps.unmodifiable(this.msgId2protoParserMap);

        loadServerConfig(configFileName);
    }

    public synchronized void loadServerConfig(String configFileName){
        AllUtil.properties2Object(configFileName, serverConfig);
    }

    public synchronized void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }


//    public ScheduledExecutorService getScheduledExecutorService() {
//        return scheduledExecutorService;
//    }
}

package org.jyg.gameserver.core.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.epoll.Epoll;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jyg.gameserver.core.data.ServerConfig;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.handle.NettyHandlerFactory;
import org.jyg.gameserver.core.manager.*;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.msg.JsonMsgCodec;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class Context {

    private static final String DEFAULT_CONFIG_FILE_NAME = "jyg.properties";
//    private String configFileName = DEFAULT_CONFIG_FILE_NAME;


    private final Map<Class<?>, Object> instanceMap = new HashMap<>();

    private final Consumer defaultConsumer;
    private final EventLoopGroupManager eventLoopGroupManager;
//    private final ExecutorManager executorManager;

    private volatile boolean isStart = false;

    private final ServerConfig serverConfig = new ServerConfig();

    private final ConsumerManager consumerManager;

    private final RemoteInvokeManager remoteInvokeManager;

//    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    //TODO I think ...
    private final SingleThreadExecutorManagerPool singleThreadExecutorManagerPool;

    private Object2IntMap<Class<? extends MessageLite>> protoClazz2MsgIdMap = new Object2IntOpenHashMap<>();
    private Int2ObjectMap<AbstractMsgCodec<?>> msgId2MsgCodecMap = new Int2ObjectOpenHashMap<>();

    private Object2IntMap<Class<? extends ByteMsgObj>> msgObjClazz2MsgIdMap = new Object2IntOpenHashMap<>();

    private final boolean useEpoll;

    private final NettyHandlerFactory nettyHandlerFactory;

    public Context(Consumer defaultConsumer) {
        this(defaultConsumer ,DEFAULT_CONFIG_FILE_NAME );
    }

    public Context(Consumer defaultConsumer , String configFileName) {
        this.defaultConsumer = defaultConsumer;

        loadServerConfig(configFileName);

        this.useEpoll = (RemotingUtil.isLinuxPlatform() && Epoll.isAvailable() && serverConfig.isPreferEpoll());


        this.eventLoopGroupManager = new EventLoopGroupManager(useEpoll);
//        this.executorManager = new ExecutorManager(10, defaultConsumer);
        this.singleThreadExecutorManagerPool = new SingleThreadExecutorManagerPool(defaultConsumer);

        defaultConsumer.setId(ConsumerManager.DEFAULT_CONSUMER_ID);
        defaultConsumer.setContext(this);
        this.consumerManager = new ConsumerManager(this);
        this.consumerManager.addConsumer(defaultConsumer);

        this.nettyHandlerFactory = new NettyHandlerFactory(this);

        this.remoteInvokeManager = new RemoteInvokeManager();

    }

    public Consumer getDefaultConsumer() {
        return defaultConsumer;
    }

    public EventLoopGroupManager getEventLoopGroupManager() {
        return eventLoopGroupManager;
    }


    public void addMsgId2ProtoMapping(int msgId, Class<? extends MessageLite> protoClazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MessageLite defaultInstance = (MessageLite)protoClazz.getMethod("getDefaultInstance").invoke(null);
        addMsgId2ProtoMapping(msgId , defaultInstance);
    }

    public void addMsgId2ProtoMapping(int msgId,  MessageLite defaultInstance) {
        ProtoMsgCodec protoMsgCodec = new ProtoMsgCodec( msgId,defaultInstance);
        this.protoClazz2MsgIdMap.put(defaultInstance.getClass(), msgId);
        this.msgId2MsgCodecMap.put(msgId ,protoMsgCodec );
    }

    public void addMsgId2JsonMsgCLassMapping(int msgId,  Class<? extends ByteMsgObj> byteMsgObjClazz) {
        JsonMsgCodec jsonMsgCodec = new JsonMsgCodec( msgId,byteMsgObjClazz);
        this.msgObjClazz2MsgIdMap.put(byteMsgObjClazz ,msgId );
        this.msgId2MsgCodecMap.put(msgId ,jsonMsgCodec );
    }



    public<T> AbstractMsgCodec<?> getMsgCodec (int msgId){

        AbstractMsgCodec<?> msgCodec = msgId2MsgCodecMap.get(msgId);
        if(msgCodec == null){
            return null;
        }

        return msgCodec;
    }


    public int getMsgIdByProtoClass( Class<? extends MessageLite> protoClass) {

        return protoClazz2MsgIdMap.getInt(protoClass);
    }

    public int getMsgIdByByteMsgObj( Class<? extends ByteMsgObj> protoClass) {

        return msgObjClazz2MsgIdMap.getInt(protoClass);
    }


    public ExecutorManager getSingleThreadExecutorManager(long playerUid) {
        return singleThreadExecutorManagerPool.getSingleThreadExecutorManager(playerUid);
    }

    public synchronized void start() {
        if(isStart){
            AllUtil.println(" already start .... ");
            return;
        }
        this.isStart = true;
        this.protoClazz2MsgIdMap = Object2IntMaps.unmodifiable(this.protoClazz2MsgIdMap);
        this.msgId2MsgCodecMap = Int2ObjectMaps.unmodifiable(this.msgId2MsgCodecMap);

        this.remoteInvokeManager.init(getServerConfig().getSacnInvokeClassPath());

//        loadServerConfig(configFileName);
    }

    public synchronized void loadServerConfig(String configFileName){
        if(isStart){
            AllUtil.println(" already start .... ");
            return;
        }
        AllUtil.properties2Object(configFileName, serverConfig);
    }


    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public boolean isStart() {
        return isStart;
    }

    public ConsumerManager getConsumerManager() {
        return consumerManager;
    }

    public NettyHandlerFactory getNettyHandlerFactory() {
        return nettyHandlerFactory;
    }


    //    public ScheduledExecutorService getScheduledExecutorService() {
//        return scheduledExecutorService;
//    }


    public boolean isUseEpoll() {
        return useEpoll;
    }


    public RemoteInvokeManager getRemoteInvokeManager() {
        return remoteInvokeManager;
    }


    @SuppressWarnings("unchecked")
    public<T> T getInstance(Class<T> tClass){
        return (T)instanceMap.get(tClass);
    }

    public void putInstance(Object obj){

        if(instanceMap.containsKey(obj.getClass())){
            throw new RuntimeException("instanceMap.containsKey(obj.getClass()) : " + obj.getClass().getName());
        }

        instanceMap.put(obj.getClass(),obj);
    }

}

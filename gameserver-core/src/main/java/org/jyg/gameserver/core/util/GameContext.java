package org.jyg.gameserver.core.util;

import com.google.protobuf.MessageLite;
import io.netty.channel.epoll.Epoll;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jyg.gameserver.core.constant.MsgIdConst;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.data.ServerConfig;
import org.jyg.gameserver.core.handle.NettyHandlerFactory;
import org.jyg.gameserver.core.manager.*;
import org.jyg.gameserver.core.msg.*;
import org.jyg.gameserver.core.msg.route.*;
import org.jyg.gameserver.core.processor.*;
import org.jyg.gameserver.core.startup.TcpClient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * create by jiayaoguang on 2020/5/3
 */
public class GameContext implements Lifecycle{

    public static final String DEFAULT_CONFIG_FILE_NAME = "jyg.properties";
//    private String configFileName = DEFAULT_CONFIG_FILE_NAME;


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

    private Object2IntMap<Class<? extends MessageLite>> protoClazz2MsgIdMap = new Object2IntOpenHashMap<>(1024,0.5f);
    private Int2ObjectMap<AbstractMsgCodec<?>> msgId2MsgCodecMap = new Int2ObjectOpenHashMap<>(1024,0.5f);

    private final Object2IntMap<Class<? extends ByteMsgObj>> msgObjClazz2MsgIdMap = new Object2IntOpenHashMap<>(1024,0.5f);




    private final boolean useEpoll;

    private final NettyHandlerFactory nettyHandlerFactory;

    private final InstanceManager instanceManager;

    private final FTLLoader ftlLoader = new FTLLoader();

    private final UidManager uidManager = new UidManager();


    private long startTime;


//    private final ClockManager clockManager = new ClockManager();


    public GameContext(Consumer defaultConsumer) {
        this(defaultConsumer ,DEFAULT_CONFIG_FILE_NAME );
    }

    public GameContext(Consumer defaultConsumer , String configFileName) {

        this.defaultConsumer = defaultConsumer;
        this.instanceManager = new InstanceManager(this);
        ConfigUtil.properties2Object(configFileName, serverConfig);

//        loadServerConfig(configFileName);

        this.useEpoll = (RemotingUtil.isLinuxPlatform() && Epoll.isAvailable() && serverConfig.isPreferEpoll());


        this.eventLoopGroupManager = new EventLoopGroupManager(useEpoll , serverConfig.getNettyIOThreadNum());
//        this.executorManager = new ExecutorManager(10, defaultConsumer);
        this.singleThreadExecutorManagerPool = new SingleThreadExecutorManagerPool(defaultConsumer);

        defaultConsumer.setId(ConsumerManager.DEFAULT_CONSUMER_ID);
        defaultConsumer.setGameContext(this);
        this.consumerManager = new ConsumerManager(this);
        this.consumerManager.addConsumer(defaultConsumer);

        this.nettyHandlerFactory = new NettyHandlerFactory(this);

        this.remoteInvokeManager = new RemoteInvokeManager();

        initCommonProcessor();

    }

    public Consumer getDefaultConsumer() {
        return defaultConsumer;
    }

    public EventLoopGroupManager getEventLoopGroupManager() {
        return eventLoopGroupManager;
    }


    public void addMsgId2ProtoMapping(int msgId, Class<? extends MessageLite> protoClazz) {
        MessageLite defaultInstance = null;
        try {
            defaultInstance = (MessageLite)protoClazz.getMethod("getDefaultInstance").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
        addMsgId2ProtoMapping(msgId , defaultInstance);
    }

    public void addMsgId2ProtoMapping(int msgId,  MessageLite defaultInstance) {
        ProtoMsgCodec protoMsgCodec = new ProtoMsgCodec(defaultInstance);
        this.protoClazz2MsgIdMap.put(defaultInstance.getClass(), msgId);
        this.msgId2MsgCodecMap.put(msgId ,protoMsgCodec );
    }

    @Deprecated
    public void addMsgId2JsonMsgClassMapping(int msgId, Class<? extends ByteMsgObj> byteMsgObjClazz) {
        addMsgId2MsgClassMapping(msgId , byteMsgObjClazz);
    }

    public void addMsgId2MsgClassMapping(int msgId, Class<? extends ByteMsgObj> byteMsgObjClazz) {

        if(isStart){
            throw new IllegalArgumentException("isStart");
        }

        AbstractMsgCodec<?> byteMsgCodec = msgId2MsgCodecMap.get(msgId);

        if(byteMsgCodec == null){
            List<Field> fieldList = AllUtil.getClassObjectFields(byteMsgObjClazz);
            if(fieldList.isEmpty()){
                byteMsgCodec = new EmptyJsonMsgCodec(byteMsgObjClazz);
            }else {
                byteMsgCodec = new JsonMsgCodec(byteMsgObjClazz);
            }

            msgId2MsgCodecMap.put(msgId , byteMsgCodec);
        }

//        if(msgId2MsgCodecMap.containsKey(byteMsgCodec.getMsgId())){
//            throw new IllegalArgumentException("msgId2MsgCodecMap.containsKey(MsgId())" + byteMsgCodec.getMsgId());
//        }

        if(msgObjClazz2MsgIdMap.containsKey(byteMsgObjClazz)){
            if(msgObjClazz2MsgIdMap.getInt(byteMsgObjClazz) !=  msgId){
                throw new IllegalArgumentException("msgObjClazz2MsgIdMap.containsKey(ByteMsgClass()" + byteMsgObjClazz);
            }
        }


        this.msgObjClazz2MsgIdMap.put(byteMsgObjClazz, msgId);
        this.msgId2MsgCodecMap.put(msgId, byteMsgCodec);
    }





    public void addByteMsgCodec(AbstractByteMsgCodec<?> byteMsgCodec) {

        if (isStart) {
            throw new IllegalArgumentException("isStart");
        }

//        if(msgId2MsgCodecMap.containsKey(byteMsgCodec.getMsgId())){
//            throw new IllegalArgumentException("msgId2MsgCodecMap.containsKey(MsgId())" + byteMsgCodec.getMsgId());
//        }


        if(!msgObjClazz2MsgIdMap.containsKey(byteMsgCodec.getByteMsgClass())){
            throw new IllegalArgumentException(" addByteMsgCodec fail, !msgObjClazz2MsgIdMap.containsKey(byteMsgCodec.getByteMsgClass())" + byteMsgCodec.getByteMsgClass());
        }

        int msgId = msgObjClazz2MsgIdMap.getInt(byteMsgCodec.getByteMsgClass());


        this.msgId2MsgCodecMap.put(msgId, byteMsgCodec);
    }


    private void initCommonProcessor(){

        addMsgId2JsonMsgClassMapping(MsgIdConst.READ_OUTTIME , ReadIdleMsgObj.class);
        addByteMsgCodec(new EmptyMsgCodec(new ReadIdleMsgObj()));

        addMsgId2JsonMsgClassMapping(MsgIdConst.REMOTE_INVOKE , RemoteInvokeData.class);

        addMsgId2JsonMsgClassMapping(MsgIdConst.PING , PingByteMsg.class);
        addByteMsgCodec(new EmptyMsgCodec(new PingByteMsg()));

        addMsgId2JsonMsgClassMapping(MsgIdConst.PONG , PongByteMsg.class);
        addByteMsgCodec(new EmptyMsgCodec(new PongByteMsg()));

        addMsgId2MsgClassMapping(MsgIdConst.ROUTE_MSG_ID , RouteMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(RouteMsg.class));

        addMsgId2MsgClassMapping(MsgIdConst.ROUTE_REPLY_MSG_ID , RouteReplyMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(RouteReplyMsg.class));

        addMsgId2MsgClassMapping(MsgIdConst.ROUTE_REGISTER_MSG_ID , RouteRegisterMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(RouteRegisterMsg.class));

        addMsgId2MsgClassMapping(MsgIdConst.ROUTE_REGISTER_REPLY_MSG_ID , RouteRegisterReplyMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(RouteRegisterReplyMsg.class));



        addMsgId2MsgClassMapping(MsgIdConst.ROUTE_CLIENT_SESSION_CONNECT_MSG_ID , RouteClientSessionConnectMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(RouteClientSessionConnectMsg.class));

        addMsgId2MsgClassMapping(MsgIdConst.ROUTE_CLIENT_SESSION_DISCONNECT_MSG_ID , RouteClientSessionDisconnectMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(RouteClientSessionDisconnectMsg.class));




        getDefaultConsumer().addProcessor(new ReadOutTimeProcessor());
        getDefaultConsumer().addProcessor(new RemoteInvokeProcessor());

        getDefaultConsumer().addProcessor(new PingProcessor());
        getDefaultConsumer().addProcessor(new PongProcessor());

        getDefaultConsumer().addProcessor(new LoadClassesHttpProcessor());
        getDefaultConsumer().addProcessor(new RedefineClassesHttpProcessor());

        getDefaultConsumer().addProcessor(new SysInfoHttpProcessor());


        getDefaultConsumer().addProcessor(new RouteMsgProcessor());
        getDefaultConsumer().addProcessor(new RouteRegisterMsgProcessor());


        getDefaultConsumer().addProcessor(new RouteClientSessionConnectProcessor());
        getDefaultConsumer().addProcessor(new RouteClientSessionDisconnectProcessor());

    }


    public<T> AbstractMsgCodec<?> getMsgCodec (int msgId){

        AbstractMsgCodec<?> msgCodec = msgId2MsgCodecMap.get(msgId);
        if(msgCodec == null){
            return null;
        }

        return msgCodec;
    }


    public int getMsgIdByMsgObj(Object msgObj) {
        if(msgObj instanceof MessageLite){
            return protoClazz2MsgIdMap.getInt(msgObj.getClass());
        }else  if(msgObj instanceof ByteMsgObj){
            return msgObjClazz2MsgIdMap.getInt(msgObj.getClass());
        }else{
            throw new IllegalArgumentException("unknown msg class :" + msgObj.getClass());
        }
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
        this.startTime = System.currentTimeMillis();

        this.protoClazz2MsgIdMap = Object2IntMaps.unmodifiable(this.protoClazz2MsgIdMap);
        this.msgId2MsgCodecMap = Int2ObjectMaps.unmodifiable(this.msgId2MsgCodecMap);

        this.remoteInvokeManager.scan(getServerConfig().getScanInvokeClassPath());

        this.instanceManager.start();

        this.getConsumerManager().start();




//        loadServerConfig(configFileName);
    }

    public synchronized void stop() {

        this.instanceManager.stop();

        for(Consumer consumer : getConsumerManager().getConsumers()){
            consumer.stop();
        }
    }

    public synchronized void loadServerConfig(String configFileName){
        if(isStart){
            AllUtil.println(" already start .... ");
            return;
        }
        ConfigUtil.properties2Object(configFileName, serverConfig);
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


    public<T> T getInstance(Class<T> tClass){
        return instanceManager.getInstance(tClass);
    }

    public void putInstance(Object obj){
        instanceManager.putInstance(obj.getClass(),obj);
    }

    public void putInstance(Class<?> clazz ) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        instanceManager.putInstance(clazz);
    }


    public InstanceManager getInstanceManager() {
        return instanceManager;
    }


    public TcpClient createTcpClient(String host , int port){
        return new TcpClient(this , host , port);
    }


    public FTLLoader getFtlLoader() {
        return ftlLoader;
    }


    public long getStartTime() {
        return startTime;
    }


    public UidManager getUidManager() {
        return uidManager;
    }


    public long nextUid(){
        return uidManager.nextUid();
    }


}

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
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.ServerConfig;
import org.jyg.gameserver.core.handle.NettyHandlerFactory;
import org.jyg.gameserver.core.intercept.HttpWhiteIpInterceptor;
import org.jyg.gameserver.core.intercept.WhiteIpInterceptor;
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
public class GameContext{

    public static final String DEFAULT_CONFIG_FILE_NAME = "ygserver.properties";
//    private String configFileName = DEFAULT_CONFIG_FILE_NAME;


    private final GameConsumer mainGameConsumer;
    private final EventLoopGroupManager eventLoopGroupManager;
//    private final ExecutorManager executorManager;

    private volatile boolean start = false;

    private volatile boolean stop = false;

    private final ServerConfig serverConfig = new ServerConfig();

    private final ConsumerManager consumerManager;


//    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    //TODO I think ...
    @Deprecated
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


    private WhiteIpInterceptor whiteIpInterceptor;

    private HttpWhiteIpInterceptor httpWhiteIpInterceptor;


    public GameContext(GameConsumer mainGameConsumer) {
        this(mainGameConsumer,DEFAULT_CONFIG_FILE_NAME );
    }

    public GameContext(GameConsumer mainGameConsumer, String configFileName) {

        ConfigUtil.properties2Object(configFileName, serverConfig);

        this.mainGameConsumer = mainGameConsumer;

        if(this.mainGameConsumer.getId() == 0){
            this.mainGameConsumer.setId(serverConfig.getServerId());
        }

//        loadServerConfig(configFileName);

        this.useEpoll = (RemotingUtil.isLinuxPlatform() && Epoll.isAvailable() && serverConfig.isPreferEpoll());



        mainGameConsumer.setGameContext(this);


        this.eventLoopGroupManager = new EventLoopGroupManager(useEpoll , serverConfig.getNettyIOThreadNum());
//        this.executorManager = new ExecutorManager(10, defaultConsumer);
        this.singleThreadExecutorManagerPool = new SingleThreadExecutorManagerPool(mainGameConsumer);
        this.consumerManager = new ConsumerManager(this);
        this.consumerManager.addConsumer(mainGameConsumer);

        this.nettyHandlerFactory = new NettyHandlerFactory(this);




        this.instanceManager = new InstanceManager();
        this.instanceManager.putInstance(this);
        this.instanceManager.putInstance(this.eventLoopGroupManager);
        this.instanceManager.putInstance(this.consumerManager);

        this.whiteIpInterceptor = new WhiteIpInterceptor();
        this.whiteIpInterceptor.addWhiteIps(this.serverConfig.getWhiteIpSet());
        this.httpWhiteIpInterceptor = new HttpWhiteIpInterceptor();
        this.httpWhiteIpInterceptor.addWhiteIps(this.serverConfig.getWhiteIpSet());




        initCommonProcessor();

    }

    public GameConsumer getMainGameConsumer() {
        return mainGameConsumer;
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

        if(start){
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

        if (start) {
            throw new IllegalArgumentException("isStart");
        }

//        if(msgId2MsgCodecMap.containsKey(byteMsgCodec.getMsgId())){
//            throw new IllegalArgumentException("msgId2MsgCodecMap.containsKey(MsgId())" + byteMsgCodec.getMsgId());
//        }


        if(!msgObjClazz2MsgIdMap.containsKey(byteMsgCodec.getByteMsgClass())){
//            throw new IllegalArgumentException(" addByteMsgCodec fail, !msgObjClazz2MsgIdMap.containsKey(byteMsgCodec.getByteMsgClass())" + byteMsgCodec.getByteMsgClass());
            Logs.DEFAULT_LOGGER.info("already contains {} , msgCodec , replace it",byteMsgCodec.getByteMsgClass().getSimpleName());
        }

        int msgId = msgObjClazz2MsgIdMap.getInt(byteMsgCodec.getByteMsgClass());


        this.msgId2MsgCodecMap.put(msgId, byteMsgCodec);
    }


    private void initCommonProcessor(){

        addMsgId2JsonMsgClassMapping(MsgIdConst.READ_OUTTIME , ReadIdleMsgObj.class);
        addByteMsgCodec(new EmptyMsgCodec(new ReadIdleMsgObj()));

//        addMsgId2JsonMsgClassMapping(MsgIdConst.REMOTE_INVOKE , RemoteInvokeData.class);

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


        addMsgId2MsgClassMapping(MsgIdConst.CONSUMER_EVENT_DATA , ConsumerEventDataMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(ConsumerEventDataMsg.class));

        addMsgId2MsgClassMapping(MsgIdConst.CONSUMER_EVENT_DATA_RETURN , ConsumerEventDataReturnMsg.class);
        addByteMsgCodec(new ProtostuffMsgCodec(ConsumerEventDataReturnMsg.class));


        getMainGameConsumer().addProcessor(new ConsumerEventDataMsgProcessor());
        getMainGameConsumer().addProcessor(new ConsumerEventDataReturnMsgProcessor());



        getMainGameConsumer().addProcessor(new ReadOutTimeProcessor());
//        getMainGameConsumer().addProcessor(new RemoteInvokeProcessor());

        getMainGameConsumer().addProcessor(new PingProcessor());
        getMainGameConsumer().addProcessor(new PongProcessor());

        getMainGameConsumer().addProcessor(new LoadClassesHttpProcessor());
        getMainGameConsumer().addProcessor(new RedefineClassesHttpProcessor());
        SysInfoHttpProcessor sysInfoHttpProcessor = new SysInfoHttpProcessor();
        sysInfoHttpProcessor.addMsgInterceptor(this.httpWhiteIpInterceptor);

        getMainGameConsumer().addProcessor(sysInfoHttpProcessor);


        getMainGameConsumer().addProcessor(new RouteMsgProcessor());
        getMainGameConsumer().addProcessor(new RouteRegisterMsgProcessor());


        getMainGameConsumer().addProcessor(new RouteClientSessionConnectProcessor());
        getMainGameConsumer().addProcessor(new RouteClientSessionDisconnectProcessor());

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
        if(start){
//            AllUtil.println(" already start .... ");
            return;
        }


        this.startTime = System.currentTimeMillis();

        this.protoClazz2MsgIdMap = Object2IntMaps.unmodifiable(this.protoClazz2MsgIdMap);
        this.msgId2MsgCodecMap = Int2ObjectMaps.unmodifiable(this.msgId2MsgCodecMap);


        this.instanceManager.start();

        this.start = true;



//        loadServerConfig(configFileName);
    }

    public synchronized void stop() {
        if(!start){
            Logs.DEFAULT_LOGGER.error("use stop method,but not start");
        }
        if(this.stop){
            return;
        }

        this.stop = true;

        this.instanceManager.stop();


    }

    public synchronized void loadServerConfig(String configFileName){
        if(start){
            AllUtil.println(" already start .... ");
            return;
        }
        ConfigUtil.properties2Object(configFileName, serverConfig);
    }


    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isStop() {
        return stop;
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



    public<T> T getInstance(Class<T> tClass){
        return instanceManager.getInstance(tClass);
    }

    public void putInstance(Object obj){
        instanceManager.putInstance(obj.getClass(),obj);
    }

    public void putInstance(Class<?> clazz ) {
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


    public int getMainConsumerId(){
        return mainGameConsumer.getId();
    }


}

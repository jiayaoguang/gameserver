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


    private Object2IntMap<Class<? extends MessageLite>> protoClazz2MsgIdMap = new Object2IntOpenHashMap<>(1024,0.5f);
    private Int2ObjectMap<AbstractMsgCodec<?>> msgId2MsgCodecMap = new Int2ObjectOpenHashMap<>(1024,0.5f);

    private final Object2IntMap<Class<? extends ByteMsgObj>> msgObjClazz2MsgIdMap = new Object2IntOpenHashMap<>(1024,0.5f);



    private int stringMsgNameCurrentIndex = 1000000;


    private Int2ObjectMap<String> msgId2StringNameMap = new Int2ObjectOpenHashMap<>(1024,0.5f);


    private final boolean useEpoll;

    private final NettyHandlerFactory nettyHandlerFactory;

    private final InstanceManager instanceManager;

    private final FTLLoader ftlLoader = new FTLLoader();

    private final UidManager uidManager = new UidManager();


    private long startTime;


//    private final ClockManager clockManager = new ClockManager();




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
        this.consumerManager = new ConsumerManager(this);
        this.consumerManager.addConsumer(mainGameConsumer);

        this.nettyHandlerFactory = new NettyHandlerFactory(this);




        this.instanceManager = new InstanceManager();
        this.instanceManager.putInstance(this);
        this.instanceManager.putInstance(this.eventLoopGroupManager);
        this.instanceManager.putInstance(this.consumerManager);




        initCommonProcessor();

    }

    public GameConsumer getMainGameConsumer() {
        return mainGameConsumer;
    }

    public EventLoopGroupManager getEventLoopGroupManager() {
        return eventLoopGroupManager;
    }



    public void addMsg(int msgId ,  Class<?> msgClazz) {
        if(MessageLite.class.isAssignableFrom(msgClazz)){
            addMsgId2ProtoMapping(msgId , (Class<? extends MessageLite>) msgClazz);
        }else if(ByteMsgObj.class.isAssignableFrom(msgClazz)){
            addMsgId2MsgClassMapping(msgId , (Class<? extends ByteMsgObj>) msgClazz);
        }else {
            throw new IllegalArgumentException("msgClazz not msg type : " + msgClazz.getName());
        }
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


    public void addMsg(String msgName,  MessageLite defaultInstance) {
        int msgId = nextStringMsgNameIndex();
        if(msgId2MsgCodecMap.containsKey(msgId)){
            throw new RuntimeException("add string msg create msgId error : " + msgName);
        }
        addMsgId2ProtoMapping(msgId,defaultInstance);
    }


    public void addMsg(String msgName,  Class<?> msgClazz) {
        int msgId = nextStringMsgNameIndex();
        addMsg(msgName,msgId,msgClazz);
    }

    public void addMsg(String msgName , int msgId,  Class<?> msgClazz) {

        if(msgId2MsgCodecMap.containsKey(msgId)){
            throw new RuntimeException("add string msg create msgId error : " + msgName);
        }


        if(msgId2StringNameMap.containsValue(msgName)){
            throw new RuntimeException("duplicate msg name : " + msgName);
        }

        msgId2StringNameMap.put(msgId,msgName);

        try{

            addMsg(msgId , msgClazz);

        }catch (Exception e){
            //添加失败，移除映射关系
            if(getMsgCodec(msgId) == null){
                msgId2StringNameMap.remove(msgId);
            }
            throw e;
        }


    }


    public String getMsgNameById(int msgId){
        return msgId2StringNameMap.get(msgId);
    }


    private int nextStringMsgNameIndex(){
        stringMsgNameCurrentIndex++;
        return stringMsgNameCurrentIndex;
    }

    public void setStringMsgNameCurrentIndex(int stringMsgNameCurrentIndex) {
        this.stringMsgNameCurrentIndex = stringMsgNameCurrentIndex;
    }

    //    @Deprecated
//    public void addMsgId2JsonMsgClassMapping(int msgId, Class<? extends ByteMsgObj> byteMsgObjClazz) {
//        addMsgId2MsgClassMapping(msgId , byteMsgObjClazz);
//    }

    public void addMsgId2MsgClassMapping(int msgId, Class<? extends ByteMsgObj> byteMsgObjClazz) {

        if(start){
            throw new IllegalArgumentException("isStart");
        }

        AbstractMsgCodec<?> byteMsgCodec = msgId2MsgCodecMap.get(msgId);

        if(byteMsgCodec == null){
            List<Field> fieldList = ClassUtil.getClassObjectFields(byteMsgObjClazz);
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

        if(!msgObjClazz2MsgIdMap.containsKey(byteMsgCodec.getByteMsgClass())){
            throw new IllegalArgumentException("msgClass not set msgid : " + byteMsgCodec.getByteMsgClass());
        }


        int msgId = msgObjClazz2MsgIdMap.getInt(byteMsgCodec.getByteMsgClass());


        this.msgId2MsgCodecMap.put(msgId, byteMsgCodec);
    }


    private void initCommonProcessor(){

        addMsgId2MsgClassMapping(MsgIdConst.READ_OUTTIME , ReadIdleMsgObj.class);
        addByteMsgCodec(new EmptyMsgCodec(new ReadIdleMsgObj()));


        addMsgId2MsgClassMapping(MsgIdConst.PING , PingByteMsg.class);
        addByteMsgCodec(new EmptyMsgCodec(new PingByteMsg()));

        addMsgId2MsgClassMapping(MsgIdConst.PONG , PongByteMsg.class);
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

        addMsgId2MsgClassMapping(MsgIdConst.GET_SERVER_TIME_REQUEST , GetServerTimeRequestMsg.class);
        addByteMsgCodec(new EmptyMsgCodec(new GetServerTimeRequestMsg()));
        addMsgId2MsgClassMapping(MsgIdConst.GET_SERVER_TIME_RESPONSE , GetServerTimeResponseMsg.class);


        getMainGameConsumer().addProcessor(new ConsumerEventDataMsgProcessor());
        getMainGameConsumer().addProcessor(new ConsumerEventDataReturnMsgProcessor());



        getMainGameConsumer().addProcessor(new ReadOutTimeProcessor());
//        getMainGameConsumer().addProcessor(new RemoteInvokeProcessor());

        getMainGameConsumer().addProcessor(new PingProcessor());
        getMainGameConsumer().addProcessor(new PongProcessor());

        getMainGameConsumer().addProcessor(new LoadClassesHttpProcessor());
        getMainGameConsumer().addProcessor(new RedefineClassesHttpProcessor());

        SysInfoHttpProcessor sysInfoHttpProcessor = new SysInfoHttpProcessor();
        getMainGameConsumer().addProcessor(sysInfoHttpProcessor);

        getMainGameConsumer().addProcessor(new MsgAccessEnableHttpProcessor());


        getMainGameConsumer().addProcessor(new RouteMsgProcessor());
        getMainGameConsumer().addProcessor(new RouteRegisterMsgProcessor());


        getMainGameConsumer().addProcessor(new RouteClientSessionConnectProcessor());
        getMainGameConsumer().addProcessor(new RouteClientSessionDisconnectProcessor());

        getMainGameConsumer().addProcessor(new GetServerTimeProcessor());
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



    public synchronized void start() {
        if(start){
//            AllUtil.println(" already start .... ");
            return;
        }
        this.start = true;

        this.startTime = System.currentTimeMillis();

        this.protoClazz2MsgIdMap = Object2IntMaps.unmodifiable(this.protoClazz2MsgIdMap);
        this.msgId2MsgCodecMap = Int2ObjectMaps.unmodifiable(this.msgId2MsgCodecMap);


        this.instanceManager.start();



        this.addStopHook();



//        loadServerConfig(configFileName);
    }

    public synchronized void stop() {
        if(!start){
            Logs.DEFAULT_LOGGER.error("use stop method,but not start");
        }
        if(this.stop){
            return;
        }


        this.instanceManager.stop();
        this.stop = true;


    }

    public synchronized void loadServerConfig(String configFileName){
        if(start){
            Logs.DEFAULT_LOGGER.warn(" already start .... ");
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



    public void addAllowRouteIp(String ip){
        {
            AbstractProcessor<RouteMsg> msgProcessor = getMainGameConsumer().getByteMsgProcessor(RouteMsg.class);
            if(msgProcessor != null && msgProcessor.getInterceptor() != null && msgProcessor.getInterceptor() instanceof WhiteIpInterceptor){
                WhiteIpInterceptor whiteIpInterceptor = (WhiteIpInterceptor) msgProcessor.getInterceptor();
                whiteIpInterceptor.addWhiteIp(ip);
            }
        }

        {
            AbstractProcessor<RouteRegisterMsg> msgProcessor = getMainGameConsumer().getByteMsgProcessor(RouteRegisterMsg.class);
            if(msgProcessor != null && msgProcessor.getInterceptor() != null && msgProcessor.getInterceptor() instanceof WhiteIpInterceptor){
                WhiteIpInterceptor whiteIpInterceptor = (WhiteIpInterceptor) msgProcessor.getInterceptor();
                whiteIpInterceptor.addWhiteIp(ip);
            }
        }

        {
            AbstractProcessor<RouteClientSessionConnectMsg> msgProcessor = getMainGameConsumer().getByteMsgProcessor(RouteClientSessionConnectMsg.class);
            if(msgProcessor != null && msgProcessor.getInterceptor() != null && msgProcessor.getInterceptor() instanceof WhiteIpInterceptor){
                WhiteIpInterceptor whiteIpInterceptor = (WhiteIpInterceptor) msgProcessor.getInterceptor();
                whiteIpInterceptor.addWhiteIp(ip);
            }
        }

        {
            AbstractProcessor<RouteClientSessionDisconnectMsg> msgProcessor = getMainGameConsumer().getByteMsgProcessor(RouteClientSessionDisconnectMsg.class);
            if(msgProcessor != null && msgProcessor.getInterceptor() != null && msgProcessor.getInterceptor() instanceof WhiteIpInterceptor){
                WhiteIpInterceptor whiteIpInterceptor = (WhiteIpInterceptor) msgProcessor.getInterceptor();
                whiteIpInterceptor.addWhiteIp(ip);
            }
        }
    }


    public void addStopHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            if(stop){
                return;
            }
            Logs.DEFAULT_LOGGER.info("exec stop shutdownHook");
            stop();
        }));
    }



    public long getSysTime(){
        return System.currentTimeMillis();
    }


}

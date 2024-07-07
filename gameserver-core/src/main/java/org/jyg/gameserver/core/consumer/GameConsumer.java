package org.jyg.gameserver.core.consumer;

import com.google.protobuf.MessageLite;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.*;
import org.jyg.gameserver.core.event.listener.GameEventListener;
import org.jyg.gameserver.core.intercept.OnlyLocalMsgInterceptor;
import org.jyg.gameserver.core.manager.*;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.processor.*;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.timer.DelayCloseTimer;
import org.jyg.gameserver.core.timer.TimerManager;
import org.jyg.gameserver.core.util.*;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public abstract class GameConsumer {

    public static final int DEFAULT_QUEUE_SIZE = 1024 * 256;

    public static final int MAP_DEFAULT_SIZE = 64;
    public static final float MAP_DEFAULT_LOADFACTOR = 0.5f;

    public static final Logger logger = Logs.CONSUMER;

    private final HttpProcessor notFoundHttpProcessor = new NotFoundHttpProcessor();

    protected volatile boolean start = false;

    private GameContext gameContext;

    private Thread thread;


    private volatile boolean stop = false;



    private final ChannelManager channelManager;

    private final Map<String, HttpProcessor> httpProcessorMap = new HashMap<>(MAP_DEFAULT_SIZE,MAP_DEFAULT_LOADFACTOR);
    private final Int2ObjectMap<AbstractProcessor> protoProcessorMap = new Int2ObjectOpenHashMap<>(MAP_DEFAULT_SIZE,MAP_DEFAULT_LOADFACTOR);




    private int id;

//    private final List<Consumer> childConsumerList = new ArrayList<>();



    private IdGenerator requestIdGenerator = new IncIdGenerator();

//    private final Map<Integer , ResultHandler> waitCallBackMap = new HashMap<>();

    protected final TimerManager timerManager;

    private final InstanceManager instanceManager;

    private final ClassLoadManager classLoadManager;

    private final ResultHandlerManager resultHandlerManager;

    private final EventManager eventManager;


    private UnknownMsgHandler unknownMsgHandler;


    private AbstractProcessor<Object> unknownProcessor;


    private RemoteMethodInvokeManager remoteMethodInvokeManager;


    public GameConsumer() {
        this.instanceManager = new InstanceManager();

        instanceManager.putInstance(GameConsumer.class , this);

        this.timerManager = new TimerManager();
        this.channelManager = new ChannelManager(this);
        this.classLoadManager = new ClassLoadManager("loadClasses");
        this.resultHandlerManager = new ResultHandlerManager();
        this.eventManager = new EventManager(this);


        this.instanceManager.putInstance(this.timerManager);
        this.instanceManager.putInstance(this.channelManager);
        this.instanceManager.putInstance(this.classLoadManager);
        this.instanceManager.putInstance(this.resultHandlerManager);
        this.instanceManager.putInstance(this.eventManager);

        this.instanceManager.putInstance(new RouteManager());

        this.instanceManager.putInstance(RemoteMethodInvokeManager.class);

        this.remoteMethodInvokeManager = this.instanceManager.getInstance(RemoteMethodInvokeManager.class);

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    protected void onThreadStart(){
        if(thread != null){
            throw new IllegalStateException("thread != null");
        }
        thread = Thread.currentThread();

        getEventManager().publishEvent(new ConsumerThreadStartEvent( this ));
    }


    public final synchronized void start(){
        beforeStart();
        if(start){
            throw new IllegalStateException("already start");
        }

        this.instanceManager.start();
        doStart();

        this.start = true;
    }

    protected void beforeStart() {

    }



    public abstract void doStart();

//    public final void beforeStop(){
//        this.instanceManager.start();
//    }

    public final synchronized void stop(){
        if(!start){
            return;
        }
        start = false;

        setStop(true);

        instanceManager.stop();

        doStop();
    }


    public abstract void doStop();

//    public void publicEvent(EventType evenType, Object data, Channel channel) {
//        publicEvent(evenType, data, channel, 0);
//    }

//    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId){
//        publicEvent(evenType, data, channel, eventId , EventData.EMPTY_EVENT_EXT_DATA);
//    }

//    public void publicEventToMain(int targetConsumerId,EventType evenType, Object data, Channel channel, int eventId , EventExtData eventExtData , ResultHandler resultHandler){
//        long requestId;
//        if(resultHandler != null){
//            requestId = registerCallBackMethod(resultHandler);
//        }else {
//            requestId = eventExtData.requestId;
//        }
//
//        getGameContext().getConsumerManager().publicEvent(targetConsumerId, evenType, data, channel, eventId, new EventExtData(getId(), requestId, eventExtData.childChooseId));
//    }

//    public void publicCallBackEvent(int targetConsumerId, Object data,  long requestId ){
//        getGameContext().getConsumerManager().publicEvent(targetConsumerId, new ResultReturnEvent(requestId , 0 , data));
//    }


    public abstract void publishEvent(EventData<?> eventData);


    public GameContext getGameContext() {
        return gameContext;
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void addHttpProcessor(HttpProcessor processor) {
        addHttpProcessor(processor.getPath(),processor);
    }

    private void addHttpProcessor(String path, HttpProcessor processor) {

        if (StringUtils.isEmpty(path) || path.charAt(0) != '/' || path.contains(".")) {
            throw new IllegalArgumentException("path cannot contain char:'.' and must start with char:'/' ");
        }

        if (httpProcessorMap.containsKey(path)) {
            throw new IllegalArgumentException("dupilcated path");
        }

        httpProcessorMap.put(path, processor);
        processor.setGameConsumer(this);
    }

    public HttpProcessor getHttpProcessor(String path) {
        HttpProcessor processor = httpProcessorMap.get(path);
        if (processor == null) {
            processor = notFoundHttpProcessor;
        }
        return processor;
    }


    public AbstractProcessor getProtoProcessor( Class<? extends MessageLite> protoClass) {

        int msgId = getGameContext().getMsgIdByProtoClass(protoClass);
        if(msgId <= 0){
            return null;
        }
        return protoProcessorMap.get(msgId);
    }

    public AbstractProcessor getByteMsgProcessor( Class<? extends ByteMsgObj> protoClass) {

        int msgId = getGameContext().getMsgIdByByteMsgObj(protoClass);
        if(msgId <= 0){
            return null;
        }
        return protoProcessorMap.get(msgId);
    }


    public AbstractProcessor getProcessor( int msgId) {

        return protoProcessorMap.get(msgId);
    }

    /**
     * 注册普通socket事件
     *
     * @param msgId     消息id
     * @param processor 事件处理器
     */
    public void addProcessor(int msgId, AbstractProcessor processor) {
        if (protoProcessorMap.containsKey(msgId)) {
            throw new IllegalArgumentException("dupilcated eventid : " + msgId);
        }
        if (processor instanceof ProtoProcessor) {
            getGameContext().addMsgId2ProtoMapping(msgId, ((ProtoProcessor) processor).getProtoClass());
        }
        if (processor instanceof ByteMsgObjProcessor) {
            getGameContext().addMsgId2MsgClassMapping(msgId, ((ByteMsgObjProcessor) processor).getByteMsgObjClazz());
        }

        processor.setGameConsumer(this);
        protoProcessorMap.put(msgId, processor);
    }


    public void processEventMsg(Session session, MsgEvent<?> event) {
        AbstractProcessor processor = protoProcessorMap.get(event.getMsgId());

        if (processor == null) {

            processor = this.unknownProcessor;
            if(processor == null){
                String name = event.getMsgData() == null ? "null" :event.getMsgData().getClass().getSimpleName();
                Logs.DEFAULT_LOGGER.info("processor not found, msgId : {} , msg : {}" , event.getMsgId() , name);
                return;
            }
        }

        String msgName;
        if(event.getMsgData() != null){
            msgName = event.getMsgData().getClass().getCanonicalName();
        }else {
            msgName = "unknown_"+event.getMsgId();
        }


        if(!processor.isEnableAccess()){
            getEventManager().publishEvent(new DisableAccessMsgEvent(session,event));
            Logs.DEFAULT_LOGGER.debug(" msg {} disable,forbid access" , msgName);
            return;
        }

        if(!processor.checkAccess(session , event)){
            getEventManager().publishEvent(new ForbidAccessMsgEvent(session,event));
            Logs.DEFAULT_LOGGER.error(" session {} forbid access msg {} " , session.getRemoteAddr() , msgName);
            return;
        }

//        Logs.DEFAULT_LOGGER.info("process msg {} msgId {}", msgName , event.getEventId());
        try{
            processor.process(session, event);
        }catch (Exception e){
            Logs.DEFAULT_LOGGER.info("process msg {} make exception : {}", msgName , ExceptionUtils.getStackTrace(e));
        }finally {
            session.recordReceiveMsgTime(event.getMsgId());
        }

    }





    public void processDefaultEvent(ConsumerDefaultEvent eventData) {

        Logs.DEFAULT_LOGGER.error("not override this method");
    }



    public void processHttpEvent(HttpRequestEvent event) {

        HttpProcessor httpProcessor = getHttpProcessor(event.getMsgData().noParamUri());

//        if(!httpProcessor.checkAccess(null , event)){
//            Logs.DEFAULT_LOGGER.info("channel {} forbid access http path {} close it", IpUtil.getChannelRemoteIp(event.getChannel()), httpProcessor.getPath());
//            event.getChannel().close();
//            return;
//        }

        if(httpProcessor == null){
            Logs.DEFAULT_LOGGER.info("channel {} access http path {} not found", IpUtil.getChannelRemoteIp(event.getChannel()), event.getMsgData().noParamUri());
        }else {
            httpProcessor.process(null, event);
        }


        //20 秒后关闭
        timerManager.addTimer(new DelayCloseTimer(event.getChannel(), 20 * 1000L));
    }


    public void initProcessors(ProcessorsInitializer processorsInitializer) {
        processorsInitializer.initProcessors(this);
    }




    public void addProcessor(AbstractProcessor<?> processor) {
        if (processor instanceof ProtoProcessor) {
            ProtoProcessor protoProcessor = (ProtoProcessor) processor;
            int msgId = getGameContext().getMsgIdByProtoClass(protoProcessor.getProtoClass());
            if(msgId <= 0){
                throw new IllegalArgumentException("class msgId not found : " + protoProcessor.getProtoClass().getCanonicalName());
            }
            addProcessor(msgId, protoProcessor);
            return;
        }
        if (processor instanceof ByteMsgObjProcessor) {
            ByteMsgObjProcessor byteMsgObjProcessor = (ByteMsgObjProcessor) processor;

            int msgId = getGameContext().getMsgIdByByteMsgObj(byteMsgObjProcessor.getByteMsgObjClazz());
            if(msgId <= 0){
                throw new IllegalArgumentException("class msgId not found : " + byteMsgObjProcessor.getByteMsgObjClazz());
            }
            addProcessor(msgId, byteMsgObjProcessor);
            return;
        }
        if (processor instanceof HttpProcessor) {
            HttpProcessor httpProcessor = (HttpProcessor) processor;
            addHttpProcessor(httpProcessor);
            return;
        }
        logger.error(" unknown processor type  : {} ", processor.getClass());
    }

    public boolean containsProcessor(AbstractProcessor<?> processor) {
        if (processor instanceof ProtoProcessor) {
            ProtoProcessor protoProcessor = (ProtoProcessor) processor;
            int msgId = getGameContext().getMsgIdByProtoClass(protoProcessor.getProtoClass());
            if(msgId <= 0){
                throw new IllegalArgumentException("class msgId not found : " + protoProcessor.getProtoClass().getCanonicalName());
            }
            if(protoProcessorMap.containsKey(msgId)){
                return true;
            }
            return false;
        }
        if (processor instanceof ByteMsgObjProcessor) {
            ByteMsgObjProcessor byteMsgObjProcessor = (ByteMsgObjProcessor) processor;

            int msgId = getGameContext().getMsgIdByByteMsgObj(byteMsgObjProcessor.getByteMsgObjClazz());
            if(msgId <= 0){
                throw new IllegalArgumentException("class msgId not found : " + byteMsgObjProcessor.getByteMsgObjClazz());
            }
            if(protoProcessorMap.containsKey(msgId)){
                return true;
            }
            return false;
        }
        if (processor instanceof HttpProcessor) {
            HttpProcessor httpProcessor = (HttpProcessor) processor;
            if(httpProcessorMap.containsKey(httpProcessor.getPath())){
                return true;
            }
            return false;
        }
        logger.error(" unknown processor type  : {} ", processor.getClass());
        return false;
    }


    public ChannelManager getChannelManager() {

//        if(!isMainConsumer()){
//            return null;
//        }

        return channelManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }





    protected void onReciveEvent(EventData<?> event) {

        // System.out.println(event.getChannel());
        try {
            long startNano = System.nanoTime();
            doEvent(event);
            long costMill = (System.nanoTime() - startNano)/1000000L;
            if(costMill > 10){
                if(event.getEvent() instanceof MsgEvent){
                    MsgEvent msgEvent = (MsgEvent)event.getEvent();
                    Logs.DEFAULT_LOGGER.error("{} event  cost more time {} data : {}",getClass().getSimpleName(), costMill, (msgEvent.getMsgData() == null? "null" :msgEvent.getMsgData().getClass().getSimpleName()));
                }else {
                    Logs.DEFAULT_LOGGER.error("{} event  cost more time {} data : {}",getClass().getSimpleName(), costMill, (event.getEvent() == null? "null" : event.getEvent().getClass().getSimpleName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            try{
//                update();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }

    }

    /**
     *
     */
    protected void update() {
        timerManager.updateTimer();
    }


    protected void doEvent(EventData event) {
        getEventManager().publishEvent(event.getEvent());
    }



    public long allocateRequestId() {
        return requestIdGenerator.nextId();
    }

    public long registerCallBackMethod(ResultHandler call) {
        long requestId = allocateRequestId();
//        new CallBackOutTimeTimer(TimeUnit.SECONDS.toMillis(5) , call);


        ResultHandlerTimeOutTimer callBackTimeOutTimer = getTimerManager().addTimer(new ResultHandlerTimeOutTimer(TimeUnit.SECONDS.toMillis(5), call, resultHandlerManager, requestId));

        resultHandlerManager.putCallBackOutTimeTimer(callBackTimeOutTimer);

//        waitCallBackMap.put(requestId , callBackWrapper);

        return requestId;
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


    public boolean isMainConsumer() {
        return getId() == gameContext.getMainConsumerId();
    }


    /**
     * 异步请求返回
     * @param targetConsumerId targetConsumerId
     * @param data data
     * @param requestId 异步请求id
     */
    public void eventReturn(int targetConsumerId, Object data, long requestId) {
        eventReturn(targetConsumerId , data , requestId , 0);
    }

    /**
     * 异步请求返回
     * @param targetConsumerId targetConsumerId
     * @param data data
     * @param requestId 异步请求id
     * @param eventId 事件类型 0 表示一切正常  其他表示有错误
     */
    public void eventReturn(int targetConsumerId, Object data,  long requestId , int eventId){
        if(requestId == 0){
            Logs.DEFAULT_LOGGER.error("eventReturn requestId == 0");
            return;
        }
        getGameContext().getConsumerManager().publicCallBackEvent(targetConsumerId , data , requestId ,eventId);
    }


    @Deprecated
    public void setConsumerStartHandler(GameEventListener<ConsumerThreadStartEvent> eventListener) {
        getEventManager().addEventListener(eventListener);
    }


    public ClassLoadManager getClassLoadManager() {
        return classLoadManager;
    }


    public EventManager getEventManager() {
        return eventManager;
    }





    public void setAllHttpLocalPermission(){
        if(start){
            throw new UnsupportedOperationException("already start");
        }

        OnlyLocalMsgInterceptor onlyLocalHttpMsgFilter = new OnlyLocalMsgInterceptor();

        for(HttpProcessor httpProcessor : httpProcessorMap.values()){
            httpProcessor.setMsgInterceptor(onlyLocalHttpMsgFilter);
        }
    }

    public UnknownMsgHandler getUnknownMsgHandler() {
        return unknownMsgHandler;
    }

    public void setUnknownMsgHandler(UnknownMsgHandler unknownMsgHandler) {
        this.unknownMsgHandler = unknownMsgHandler;
    }


    public void setUnknownProcessor(AbstractProcessor<Object> unknownProcessor) {
        this.unknownProcessor = unknownProcessor;
        this.unknownProcessor.setGameConsumer(this);
    }


    public ResultHandlerManager getResultHandlerManager() {
        return resultHandlerManager;
    }


    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }


    public RemoteMethodInvokeManager getRemoteMethodInvokeManager() {
        return remoteMethodInvokeManager;
    }
}

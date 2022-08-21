package org.jyg.gameserver.core.consumer;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.anno.InvokeName;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.event.ConsumerThreadStartEvent;
import org.jyg.gameserver.core.event.EventManager;
import org.jyg.gameserver.core.filter.OnlyLocalHttpMsgFilter;
import org.jyg.gameserver.core.manager.*;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.processor.*;
import org.jyg.gameserver.core.session.LocalSession;
import org.jyg.gameserver.core.session.MQSession;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.timer.DelayCloseTimer;
import org.jyg.gameserver.core.timer.TimerManager;
import org.jyg.gameserver.core.util.*;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public abstract class Consumer {

    public static final int DEFAULT_QUEUE_SIZE = 1024 * 64;
    public static final int DEFAULT_CONSUMER_ID = 1;

    public static final int MAP_DEFAULT_SIZE = 64;
    public static final float MAP_DEFAULT_LOADFACTOR = 0.5f;

    public static final Logger logger = Logs.CONSUMER;

    private final HttpProcessor notFoundHttpProcessor = new NotFoundHttpProcessor();

    protected volatile boolean isStart = false;

    private GameContext gameContext;

    private Thread thread;




    private final ChannelManager channelManager;

    private Map<String, HttpProcessor> httpProcessorMap = new HashMap<>(MAP_DEFAULT_SIZE,MAP_DEFAULT_LOADFACTOR);
    private Int2ObjectMap<Processor> protoProcessorMap = new Int2ObjectOpenHashMap<>(MAP_DEFAULT_SIZE,MAP_DEFAULT_LOADFACTOR);

    private TextProcessor textProcessor;



    private int id;

//    private final List<Consumer> childConsumerList = new ArrayList<>();

    private int requestId = 1;


//    private final Map<Integer , ResultHandler> waitCallBackMap = new HashMap<>();

    protected final TimerManager timerManager;

    private final InstanceManager instanceManager;

    private final ClassLoadManager classLoadManager;

    private final ResultHandlerManager resultHandlerManager;

    private final EventManager eventManager;

    private AbstractProcessor defaultProcessor = null;


    private UnknownMsgHandler unknownMsgHandler;


    public Consumer() {
        this.instanceManager = new InstanceManager(this);
        this.timerManager = new TimerManager();
        this.channelManager = new ChannelManager(this);
        this.classLoadManager = new ClassLoadManager("loadClasses");
        this.resultHandlerManager = new ResultHandlerManager();
        this.eventManager = new EventManager();


        instanceManager.putInstance(this.timerManager);
        instanceManager.putInstance(this.channelManager);
        instanceManager.putInstance(this.classLoadManager);
        instanceManager.putInstance(this.resultHandlerManager);
        instanceManager.putInstance(this.eventManager);

        this.instanceManager.putInstance(new RouteManager());


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

        getEventManager().triggerEvent(ConsumerThreadStartEvent.class ,this);
    }

    public synchronized final void start(){
        beforeStart();
        this.isStart = true;
        this.instanceManager.start();
        doStart();
    }

    public void beforeStart() {

    }



    public abstract void doStart();

//    public final void beforeStop(){
//        this.instanceManager.start();
//    }

    public synchronized final void stop(){
        instanceManager.stop();
        channelManager.stop();
        doStop();
    }


    public abstract void doStop();

//    public void publicEvent(EventType evenType, Object data, Channel channel) {
//        publicEvent(evenType, data, channel, 0);
//    }

    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId){
        publicEvent(evenType, data, channel, eventId , EventData.EMPTY_EVENT_EXT_DATA);
    }

    public void publicEventToTarget(int targetConsumerId,EventType evenType, Object data, Channel channel, int eventId , EventExtData eventExtData , ResultHandler resultHandler){
        int requestId = 0;
        if(resultHandler != null){
            requestId = registerCallBackMethod(resultHandler);
        }else {
            requestId = eventExtData.requestId;
        }

        getGameContext().getConsumerManager().publicEvent(targetConsumerId, evenType, data, channel, eventId, new EventExtData(getId(), requestId, eventExtData.childChooseId));
    }

    public void publicCallBackEventToTarget(int targetConsumerId, Object data,  int requestId ){
        getGameContext().getConsumerManager().publicEvent(targetConsumerId, EventType.RESULT_CALL_BACK, data, null, 0, new EventExtData(0, requestId, 0));
    }

    public abstract void publicEvent(EventType evenType, Object data, Channel channel, int eventId , EventExtData eventExtData);




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
        processor.setConsumer(this);
    }

    public HttpProcessor getHttpProcessor(String path) {
        HttpProcessor processor = httpProcessorMap.get(path);
        if (processor == null) {
            processor = notFoundHttpProcessor;
        }
        return processor;
    }

    /**
     * 注册普通socket事件
     *
     * @param msgId     消息id
     * @param processor 事件处理器
     */
    public void addProcessor(int msgId, AbstractProcessor processor) {
        if (protoProcessorMap.containsKey(msgId)) {
            throw new IllegalArgumentException("dupilcated eventid");
        }
        if (processor instanceof ProtoProcessor) {
            getGameContext().addMsgId2ProtoMapping(msgId, ((ProtoProcessor) processor).getProtoClass());
        }
        if (processor instanceof ByteMsgObjProcessor) {
            getGameContext().addMsgId2MsgClassMapping(msgId, ((ByteMsgObjProcessor) processor).getByteMsgObjClazz());
        }

        processor.setConsumer(this);
        protoProcessorMap.put(msgId, processor);
    }

    /**
     * 注册普通socket事件
     *
     * @param textProcessor textProcessor
     */
    @Deprecated
    public void setTextProcessor(TextProcessor textProcessor) {
        textProcessor.setConsumer(this);
        this.textProcessor = textProcessor;

    }


    /**
     * 处理普通socket事件
     *
     * @param session session
     * @param event   event
     */
    public void processEventMsg(Session session, EventData<?> event) {
//		MessageLite msg = event.getData();
        Processor processor = protoProcessorMap.get(event.getEventId());


        if(processor == null){

            processor = defaultProcessor;

            if (processor == null) {
                String name = event.getData() == null ? "null" :event.getData().getClass().getSimpleName();
                Logs.DEFAULT_LOGGER.info("processor not found, eventid : {} , msg : {}" , event.getEventId() , name);
                return;
            }
        }

        String msgName;
        if(event.getData() != null){
            msgName = event.getData().getClass().getCanonicalName();
        }else {
            msgName = "unknown";
        }

        if(!processor.checkFilters(session , event)){

            Logs.DEFAULT_LOGGER.info("refuse processor msg {}", msgName);
            return;
        }

//        Logs.DEFAULT_LOGGER.info("process msg {} msgId {}", msgName , event.getEventId());
        processor.process(session, event);
    }




    protected void processDefaultEvent(int eventId , EventData eventData) {


    }


    /**
     * 处理普通socket事件
     *
     * @param session session
     * @param event   event
     */
    @Deprecated
    public void processTextEvent(Session session, EventData<String> event) {
//		MessageLite msg = event.getData();

        if (textProcessor == null) {
            Logs.DEFAULT_LOGGER.info("textProcessor == null :" + event.getEventId());
            return;
        }

        textProcessor.process(session, event);
    }

    public void processHttpEvent(EventData<Request> event) {

        HttpProcessor httpProcessor = getHttpProcessor(event.getData().noParamUri());

        if(!httpProcessor.checkFilters(null , event)){
            String msgName = httpProcessor.getPath();
            Logs.DEFAULT_LOGGER.info("refuse httpProcessor path {}", httpProcessor.getPath());
            return;
        }


        httpProcessor.process(null, event);
        //六十秒后关闭
        timerManager.addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
    }


    public void initProcessors(ProcessorsInitializer processorsInitializer) {
        processorsInitializer.initProcessors(this);
    }




    public void addProcessor(Processor<?> processor) {
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

    public boolean containsProcessor(Processor<?> processor) {
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

        if(!isDefaultConsumer()){
            return null;
        }

        return channelManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }


    public IRemoteInvoke createRemoteInvoke(Class<? extends IRemoteInvoke> remoteInvokeClass, TcpClient tcpClient) {

        String invokeName;

        InvokeName invokeNameAnno = remoteInvokeClass.getAnnotation(InvokeName.class);
        if (invokeNameAnno != null) {
            invokeName = invokeNameAnno.name();
        } else {
            invokeName = remoteInvokeClass.getName();
        }

        return createRemoteInvoke(invokeName , tcpClient);
    }




    @Deprecated
    public IRemoteInvoke createRemoteInvoke(final String remoteInvokeName, TcpClient tcpClient) {

        IRemoteInvoke remoteInvoke = new IRemoteInvoke() {
            @Override
            public void invoke(Consumer consumer,Map<String,Object> paramMap) {

                try {

                    RemoteInvokeData remoteInvokeData = new RemoteInvokeData();
                    remoteInvokeData.setConsumerId(getId());

                    remoteInvokeData.setInvokeName(remoteInvokeName);

                    remoteInvokeData.setParamMap(paramMap);

                    tcpClient.write(remoteInvokeData);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        return remoteInvoke;
    }



    public void invokeRemoteFunc(final String remoteInvokeName , int targetConsumer,Map<String,Object> paramMap, TcpClient tcpClient) {


        RemoteInvokeData remoteInvokeData = new RemoteInvokeData();
        remoteInvokeData.setConsumerId(getId());
        remoteInvokeData.setConsumerId(targetConsumer);
        remoteInvokeData.setInvokeName(remoteInvokeName);

        remoteInvokeData.setParamMap(paramMap);

        tcpClient.write(remoteInvokeData);

    }



    protected void onReciveEvent(EventData<?> event) {

        // System.out.println(event.getChannel());
        try {
            long startNano = System.nanoTime();
            doEvent(new EventData(event.getChannel(), event.getEventType(), event.getData(), event.getEventExtData(), event.getEventId()));
            long costMill = (System.nanoTime() - startNano)/1000000L;
            if(costMill > 10){
                Logs.DEFAULT_LOGGER.error("{} event  cost more time {} data : {}",getClass().getSimpleName(), costMill, (event.getData() == null? "null" : event.getData().getClass().getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                update();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     *
     */
    protected void update() {
        timerManager.updateTimer();
    }


    private void doEvent(EventData event) {
        switch (event.getEventType()) {

//			case CLIENT_SOCKET_CONNECT_ACTIVE:
//				dispatcher.as_on_client_active(event);
//				break;
//			case CLIENT_SOCKET_CONNECT_INACTIVE:
//				dispatcher.as_on_client_inactive(event);
//				break;

            case SOCKET_CONNECT_ACTIVE:
//				dispatcher.as_on_inner_server_active(event);
                if (isDefaultConsumer()) {
                    channelManager.doLink(event.getChannel());
                }else {
                    Logs.DEFAULT_LOGGER.error("event SOCKET_CONNECT_ACTIVE only in DefaultConsumer");
                }
                break;
            case SOCKET_CONNECT_INACTIVE:
                if (isDefaultConsumer()) {
                    channelManager.doUnlink(event.getChannel());
                }else {
                    Logs.DEFAULT_LOGGER.error("event SOCKET_CONNECT_INACTIVE only in DefaultConsumer");
                }
                break;

            case HTTP_MESSAGE_COME:
                ((Request) event.getData()).setRequestid(getAndIncRequestId());
                this.processHttpEvent(event);
				event.getChannel().close();
                // 5秒后关闭
//				dispatcher.getTimerManager().addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
                break;
//            case ON_MESSAGE_COME:
//				dispatcher.webSocketProcess(event);
//				break;
            case REMOTE_MSG_COME:{
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                this.processEventMsg(session, event);
                break;
            }
            case MQ_MSG_COME:{
                Session session = new MQSession(event.getFromConsumerId(), gameContext);
                this.processEventMsg(session, event);
                break;
            }

            case TEXT_MESSAGE_COME: {
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                this.processTextEvent(session, event);
                break;
            }

            case INNER_MSG:
                doInnerMsg(event.getData());
                break;

            case DEFAULT_EVENT:
                processDefaultEvent(event.getEventId() ,event);
                break;
            case RESULT_CALL_BACK:
                resultHandlerManager.onCallBack(event.getEventExtData().requestId, event.getEventId(), event.getData());
                break;

            case CLIENT_SOCKET_CONNECT_ACTIVE:
                if (isDefaultConsumer()) {
                    channelManager.doTcpClientLink(event.getChannel());
                }else {
                    Logs.DEFAULT_LOGGER.error("event CLIENT_SOCKET_CONNECT_ACTIVE only in DefaultConsumer");
                }
                break;
            case CLIENT_SOCKET_CONNECT_INACTIVE:
                if (isDefaultConsumer()) {
                    channelManager.doTcpClientUnlink(event.getChannel());
                }else {
                    Logs.DEFAULT_LOGGER.error("event SOCKET_CONNECT_INACTIVE only in DefaultConsumer");
                }
                break;

            case LOCAL_MSG_COME:{
                Session session = new LocalSession(event.getFromConsumerId(), gameContext);
                this.processEventMsg(session, event);
                break;
            }
            case REMOTE_UNKNOWN_MSG_COME:{
                if(unknownMsgHandler != null){
                    Session session = null;
                    if (isDefaultConsumer()) {
                        session = channelManager.getSession(event.getChannel());
                    }
                    unknownMsgHandler.process(session, event.getEventId(), (byte[]) event.getData());
                }else {
                    Logs.CONSUMER.error(" unknown msgId {} from channel {}", event.getEventId(), AllUtil.getChannelRemoteAddr(event.getChannel()));
                }
                break;
            }

            default:
                throw new IllegalArgumentException("unknown channelEventType <" + event.getEventType() + ">");
        }
    }

    private void doInnerMsg(Object msg) {
        if (!(msg instanceof CallBackEvent)) {
            return;
        }
        CallBackEvent callBackEvent = (CallBackEvent) msg;
        callBackEvent.execte();
    }


    private int getAndIncRequestId() {
        if (requestId == Integer.MAX_VALUE) {
            requestId = 1;
        }
        return requestId++;
    }

    public int registerCallBackMethod(ResultHandler call) {
        int requestId = getAndIncRequestId();
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

    public void putInstance(Class<?> clazz ) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        instanceManager.putInstance(clazz);
    }


    public InstanceManager getInstanceManager() {
        return instanceManager;
    }


    public boolean isDefaultConsumer() {
        return getId() == DEFAULT_CONSUMER_ID;
    }


    /**
     * 异步请求返回
     * @param targetConsumerId targetConsumerId
     * @param data data
     * @param requestId 异步请求id
     */
    public void eventReturn(int targetConsumerId, Object data, int requestId) {
        eventReturn(targetConsumerId , data , requestId , 0);
    }

    /**
     * 异步请求返回
     * @param targetConsumerId targetConsumerId
     * @param data data
     * @param requestId 异步请求id
     * @param eventId 事件类型 0 表示一切正常  其他表示有错误
     */
    public void eventReturn(int targetConsumerId, Object data,  int requestId , int eventId){
        if(requestId == 0){
            Logs.DEFAULT_LOGGER.error("eventReturn requestId == 0");
            return;
        }
        getGameContext().getConsumerManager().publicCallBackEvent(targetConsumerId , data , requestId ,eventId);
    }


    @Deprecated
    public void setConsumerStartHandler(ConsumerStartHandler consumerStartHandler) {
        getEventManager().addEvent(new ConsumerThreadStartEvent((a,b)->{ consumerStartHandler.onThreadStart(this); }));
    }


    public ClassLoadManager getClassLoadManager() {
        return classLoadManager;
    }


    public EventManager getEventManager() {
        return eventManager;
    }



    public void setDefaultProcessor(AbstractProcessor defaultProcessor ){
        defaultProcessor.setConsumer(this);
        this.defaultProcessor = defaultProcessor;
    }



    public void setAllHttpLocalPermission(){
        if(isStart){
            throw new RuntimeException("already start");
        }

        OnlyLocalHttpMsgFilter onlyLocalHttpMsgFilter = new OnlyLocalHttpMsgFilter();

        for(HttpProcessor httpProcessor : httpProcessorMap.values()){
            httpProcessor.addMsgFilter(onlyLocalHttpMsgFilter);
        }
    }


    public void setUnknownMsgHandler(UnknownMsgHandler unknownMsgHandler) {
        this.unknownMsgHandler = unknownMsgHandler;
    }
}

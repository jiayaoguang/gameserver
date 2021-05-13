package org.jyg.gameserver.core.consumer;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.anno.InvokeName;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteInvokeData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.manager.ChannelManager;
import org.jyg.gameserver.core.manager.InstanceManager;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.processor.*;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.timer.DelayCloseTimer;
import org.jyg.gameserver.core.timer.TimerManager;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public abstract class Consumer {

    public static final int DEFAULT_QUEUE_SIZE = 1024 * 64;

    public static final Logger logger = LoggerFactory.getLogger("Consumer");

    private final HttpProcessor notFOundProcessor = new NotFoundHttpProcessor();

    private Context context;


    protected final TimerManager timerManager = new TimerManager();

    private final ChannelManager channelManager = new ChannelManager();

    private Map<String, HttpProcessor> httpProcessorMap = new HashMap<>();
    private Int2ObjectMap<Processor> protoProcessorMap = new Int2ObjectOpenHashMap<>();

    private TextProcessor textProcessor;

    private final InstanceManager instanceManager;

    private int id;

    private final List<Consumer> childConsumerList = new ArrayList<>();


    public static final int DEFAULT_CONSUMER_ID = 0;

    private int requestId = 1;

    public Consumer() {
        this.instanceManager = new InstanceManager();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public final void start(){
        this.instanceManager.start();
        doStart();
    }



    public abstract void doStart();

    public final void beforeStop(){
        this.instanceManager.start();
    }

    public abstract void stop();

    public void publicEvent(EventType evenType, Object data, Channel channel) {
        publicEvent(evenType, data, channel, 0);
    }

    public abstract void publicEvent(EventType evenType, Object data, Channel channel, int eventId);


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void addHttpProcessor(String path, HttpProcessor processor) {

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
            processor = notFOundProcessor;
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
            getContext().addMsgId2ProtoMapping(msgId, ((ProtoProcessor) processor).getProtoDefaultInstance());
        }
        if (processor instanceof ByteMsgObjProcessor) {
            getContext().addMsgId2JsonMsgCLassMapping(msgId, ((ByteMsgObjProcessor) processor).getByteMsgObjClazz());
        }

        processor.setConsumer(this);
        protoProcessorMap.put(msgId, processor);
    }

    /**
     * 注册普通socket事件
     *
     * @param textProcessor textProcessor
     */
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
    public void processEventMsg(Session session, EventData<? extends MessageLite> event) {
//		MessageLite msg = event.getData();
        Processor processor = protoProcessorMap.get(event.getEventId());
        if (processor == null) {
            Logs.DEFAULT_LOGGER.info("unknown socket eventid :" + event.getEventId());
            return;
        }

        processor.process(session, event);
    }


    /**
     * 处理普通socket事件
     *
     * @param session session
     * @param event   event
     */
    public void processTextEvent(Session session, EventData<String> event) {
//		MessageLite msg = event.getData();

        if (textProcessor == null) {
            Logs.DEFAULT_LOGGER.info("textProcessor == null :" + event.getEventId());
            return;
        }

        textProcessor.process(session, event);
    }

    public void processHttpEvent(EventData<Request> event) {
        getHttpProcessor(event.getData().noParamUri()).process(null, event);
        //六十秒后关闭
        timerManager.addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
    }


    public void initChildConsumers(int num, ProcessorsInitializer processorsInitializer) {
        if (context != null && context.isStart()) {
            return;
        }
        for (int i = 0; i < num; i++) {
            Consumer consumer = new BlockingQueueConsumer();
            processorsInitializer.initProcessors(consumer);
            childConsumerList.add(consumer);
        }
    }

    public void initProcessors(ProcessorsInitializer processorsInitializer) {
        processorsInitializer.initProcessors(this);
    }

    public void addChildConsumer(Consumer consumer, ProcessorsInitializer processorsInitializer) {
        processorsInitializer.initProcessors(consumer);
        addChildConsumer(consumer);
    }

    public void addChildConsumer(Consumer consumer) {
        if (context != null && context.isStart()) {
            return;
        }
        childConsumerList.add(consumer);
    }

    public Consumer getChildConsumer(int num) {
        if (childConsumerList.isEmpty()) {
            return null;
        }
        return childConsumerList.get(num % childConsumerList.size());
    }

    public void addProcessor(Processor<?> processor) {
        if (processor instanceof ProtoProcessor) {
            ProtoProcessor protoProcessor = (ProtoProcessor) processor;
            addProcessor(protoProcessor.getProtoMsgId(), protoProcessor);
            return;
        }
        if (processor instanceof ByteMsgObjProcessor) {
            ByteMsgObjProcessor byteMsgObjProcessor = (ByteMsgObjProcessor) processor;
            addProcessor(byteMsgObjProcessor.getMsgId(), byteMsgObjProcessor);
            return;
        }
        if (processor instanceof HttpProcessor) {
            HttpProcessor httpProcessor = (HttpProcessor) processor;
            addHttpProcessor(httpProcessor.getPath(), httpProcessor);
            return;
        }
        logger.error(" unknown processor type  : {} ", processor.getClass());
    }



    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }


    public IRemoteInvoke createRemoteInvoke(Class<? extends IRemoteInvoke> remoteInvokeClass, Session session) {

        String invokeName;

        InvokeName invokeNameAnno = remoteInvokeClass.getAnnotation(InvokeName.class);
        if (invokeNameAnno != null) {
            invokeName = invokeNameAnno.name();
        } else {
            invokeName = remoteInvokeClass.getName();
        }

        return createRemoteInvoke(invokeName , session);
    }




    public IRemoteInvoke createRemoteInvoke(final String remoteInvokeName, Session session) {

        IRemoteInvoke remoteInvoke = new IRemoteInvoke() {
            @Override
            public void invoke(JSONObject paramJson) {

                try {

                    RemoteInvokeData remoteInvokeData = new RemoteInvokeData();
                    remoteInvokeData.setConsumerId(getId());

                    remoteInvokeData.setInvokeName(remoteInvokeName);

                    remoteInvokeData.setParamJson(paramJson);

                    session.writeMessage(remoteInvokeData);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        return remoteInvoke;
    }



    public final void onReciveEvent(EventData<?> event) {

        // System.out.println(event.getChannel());
        try {
            doEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            update();
        }

    }

    /**
     *
     */
    protected void update() {
        timerManager.updateTimer();
    }

    private void doEvent(EventData event) {
        switch (event.getChannelEventType()) {

//			case CLIENT_SOCKET_CONNECT_ACTIVE:
//				dispatcher.as_on_client_active(event);
//				break;
//			case CLIENT_SOCKET_CONNECT_INACTIVE:
//				dispatcher.as_on_client_inactive(event);
//				break;

            case SOCKET_CONNECT_ACTIVE:
//				dispatcher.as_on_inner_server_active(event);
                if (isDefaultConsumer()) {
                    channelManager.doLink(event);
                }
                break;
            case SOCKET_CONNECT_INACTIVE:
                if (isDefaultConsumer()) {
                    channelManager.doUnlink(event);
                }
                break;

            case HTTP_MESSAGE_COME:
                ((Request) event.getData()).setRequestid(getAndIncRequestId());
                context.getDefaultConsumer().processHttpEvent(event);
//				event.getChannel().close();
                // 5秒后关闭
//				dispatcher.getTimerManager().addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
                break;
//            case ON_MESSAGE_COME:
//				dispatcher.webSocketProcess(event);
//				break;
            case RROTO_MSG_COME: {
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                context.getDefaultConsumer().processEventMsg(session, event);
                break;
            }
            case BYTE_OBJ_MSG_COME:{
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                context.getDefaultConsumer().processEventMsg(session, event);
                break;
            }

            case TEXT_MESSAGE_COME: {
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                context.getDefaultConsumer().processTextEvent(session, event);
                break;
            }

            case INNER_MSG:
                doInnerMsg(event.getData());
                break;

            default:
                throw new IllegalArgumentException("unknown channelEventType <" + event.getChannelEventType() + ">");
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
            requestId = 0;
        }
        return requestId++;
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

}

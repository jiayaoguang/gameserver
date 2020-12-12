package org.jyg.gameserver.core.consumer;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.manager.ChannelManager;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.processor.*;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.timer.DelayCloseTimer;
import org.jyg.gameserver.core.timer.TimerManager;
import org.jyg.gameserver.core.util.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected ConsumerHandlerFactory eventConsumerFactory;


    protected final TimerManager timerManager = new TimerManager();

    private Map<String, HttpProcessor> httpProcessorMap = new HashMap<>();
    private Int2ObjectMap<Processor> protoProcessorMap = new Int2ObjectOpenHashMap<>();

    private TextProcessor textProcessor;

    protected ConsumerHandler consumerHandler;

    private int id;

    private final List<Consumer> childConsumerList = new ArrayList<>() ;

    public Consumer() {

    }

    public Consumer(ConsumerHandler consumerHandler) {
        this.consumerHandler = consumerHandler;
    }

    public Consumer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract void start();

    public abstract void stop();

    public void publicEvent(EventType evenType, Object data, Channel channel){
        publicEvent(evenType, data, channel, 0);
    }

    public abstract void publicEvent(EventType evenType, Object data, Channel channel, int eventId);


    public ConsumerHandlerFactory getEventConsumerFactory() {
        return eventConsumerFactory;
    }

    public void setEventConsumerFactory(ConsumerHandlerFactory eventConsumerFactory) {
        this.eventConsumerFactory = eventConsumerFactory;
    }

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
    public void addProcessor(int msgId, AbstractProcessor processor ) {
        if (protoProcessorMap.containsKey(msgId)) {
            throw new IllegalArgumentException("dupilcated eventid");
        }
        if(processor instanceof ProtoProcessor){
            getContext().addMsgId2ProtoMapping(msgId , ((ProtoProcessor)processor).getProtoDefaultInstance());
        }
        if(processor instanceof ByteMsgObjProcessor){
            getContext().addMsgId2JsonMsgCLassMapping(msgId , ((ByteMsgObjProcessor)processor).getByteMsgObjClazz());
        }

        processor.setConsumer(this);
        protoProcessorMap.put(msgId, processor);
    }

    /**
     * 注册普通socket事件
     * @param textProcessor textProcessor
     */
    public void setTextProcessor(TextProcessor textProcessor) {
        textProcessor.setConsumer(this);
        this.textProcessor = textProcessor;

    }


    /**
     * 处理普通socket事件
     * @param session session
     * @param event event
     */
    public void processEventMsg(Session session , LogicEvent<? extends MessageLite> event) {
//		MessageLite msg = event.getData();
        Processor processor = protoProcessorMap.get(event.getEventId());
        if (processor == null) {
            System.out.println("unknown socket eventid :" + event.getEventId());
            return;
        }

        processor.process(session , event);
    }


    /**
     * 处理普通socket事件
     * @param session session
     * @param event event
     */
    public void processTextEvent(Session session , LogicEvent<String> event) {
//		MessageLite msg = event.getData();

        if (textProcessor == null) {
            System.out.println("textProcessor == null :" + event.getEventId());
            return;
        }

        textProcessor.process(session , event);
    }

    public void processHttpEvent(LogicEvent<Request> event) {
        getHttpProcessor(event.getData().noParamUri()).process(null , event);
        //六十秒后关闭
        timerManager.addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
    }


    public void initChildConsumers(int num , ProcessorsInitializer processorsInitializer) {
        if(context!=null && context.isStart()){
            return;
        }
        for (int i = 0; i < num; i++) {
            Consumer consumer = new BlockingConsumer();
            processorsInitializer.initProcessors(consumer);
            childConsumerList.add(consumer);
        }
    }

    public void initProcessors(ProcessorsInitializer processorsInitializer){
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

    public Consumer getChildConsumer(int num){
        if(childConsumerList.isEmpty()){
            return null;
        }
        return childConsumerList.get( num % childConsumerList.size());
    }

    public void addProcessor(Processor<?> processor) {
        if(processor instanceof ProtoProcessor){
            ProtoProcessor protoProcessor = (ProtoProcessor)processor;
            addProcessor(protoProcessor.getProtoMsgId() , protoProcessor );
            return;
        }
        if(processor instanceof ByteMsgObjProcessor){
            ByteMsgObjProcessor byteMsgObjProcessor = (ByteMsgObjProcessor)processor;
            addProcessor(byteMsgObjProcessor.getMsgId() , byteMsgObjProcessor );
            return;
        }
        if(processor instanceof HttpProcessor){
            HttpProcessor httpProcessor = (HttpProcessor)processor;
            addHttpProcessor(httpProcessor.getPath() , httpProcessor );
            return;
        }
        logger.error(" unknown processor type  : {} " , processor.getClass());
    }


    public ConsumerHandler getConsumerHandler() {
        return consumerHandler;
    }

    public ChannelManager getChannelManager() {
        return consumerHandler.getChannelManager();
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }
}

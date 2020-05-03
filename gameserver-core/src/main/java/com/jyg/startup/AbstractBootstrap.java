package com.jyg.startup;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.net.Request;
import com.jyg.processor.HttpProcessor;
import com.jyg.processor.Processor;
import com.jyg.processor.ProtoProcessor;
import com.jyg.util.Context;
import com.jyg.util.IGlobalQueue;
import com.jyg.util.RingBufferGlobalQueue;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create by jiayaoguang at 2018年3月5日
 * 抽象启动类
 */

public abstract class AbstractBootstrap {

    protected Int2ObjectMap<ProtoProcessor<? extends GeneratedMessageV3>> protoProcessorMap = new Int2ObjectOpenHashMap<>();

    protected Int2ObjectMap<Class<? extends GeneratedMessageV3>> eventId2ProtoClassMap = new Int2ObjectOpenHashMap<>();

    protected Object2ObjectMap<String, Processor<Request>> httpProcessorMap = new Object2ObjectOpenHashMap<>();

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final IGlobalQueue globalQueue;

    private final Context context;

    protected boolean isStart = false;

    public AbstractBootstrap() {
        this(new RingBufferGlobalQueue());
    }

    public AbstractBootstrap(IGlobalQueue globalQueue) {
        this(new Context(globalQueue));
    }

    public AbstractBootstrap(Context context) {
        this.globalQueue = context.getGlobalQueue();
        this.context = context;
        this.globalQueue.getEventConsumerFactory().setEventDispatcher(context.getEventDispatcher());
    }

    public void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory) {
        if (isStart) {
            logger.error("oprete fail,server is already start ");
            return;
        }
        eventConsumerFactory.setEventDispatcher(this.context.getEventDispatcher());
        this.globalQueue.setEventConsumerFactory(eventConsumerFactory);

    }

    public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoProcessor) {

        protoProcessorMap.put(eventid, protoProcessor);

        this.context.getEventDispatcher().registerSocketEvent(eventid, protoProcessor);
    }


    public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
        if (isStart) {
            logger.error("oprete fail,server is already start ");
            return;
        }

        httpProcessorMap.put(path, processor);

        this.context.getEventDispatcher().registerHttpEvent(path, processor);
    }

    public void registerSendEventIdByProto(int eventId, Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
        if (isStart) {
            logger.error("oprete fail,server is already start ");
            return;
        }

        eventId2ProtoClassMap.put(eventId, protoClazz);

        this.context.getEventDispatcher().registerSendEventIdByProto(eventId, protoClazz);
    }

    public Logger getLogger() {
        return logger;
    }

    public IGlobalQueue getGlobalQueue() {
        return globalQueue;
    }

    public Context getContext() {
        return context;
    }

    public abstract void start() throws InterruptedException;


}

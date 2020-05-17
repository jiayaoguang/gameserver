package org.jyg.gameserver.core.startup;

import com.google.protobuf.GeneratedMessageV3;
import org.jyg.gameserver.core.consumer.EventConsumerFactory;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.IGlobalQueue;
import org.jyg.gameserver.core.util.RingBufferGlobalQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create by jiayaoguang at 2018年3月5日
 * 抽象启动类
 */

public abstract class AbstractBootstrap {

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
        this.registerProtoProcessor(eventid, protoProcessor);
    }

    public void registerProtoProcessor(int eventid, ProtoProcessor<? extends GeneratedMessageV3> protoProcessor) {
        this.context.getEventDispatcher().registerSendEventIdByProto( eventid , protoProcessor.getProtoClass() );
//        this.registerProtoProcessor( protoProcessor);
    }

    public void registerProtoProcessor(ProtoProcessor<? extends GeneratedMessageV3> protoProcessor) {
        int eventId = protoProcessor.getProtoEventId();
        if(eventId == -1){
            throw new IllegalArgumentException(" getProtoEventId -1 ");
        }
        this.context.getEventDispatcher().registerSocketEvent(protoProcessor.getProtoEventId(), protoProcessor);
    }

    public void registerHttpEvent(String path, HttpProcessor processor){
        processor.setPath(path);
        this.registerHttpProcessor( processor);
    }

    public void registerHttpProcessor( HttpProcessor processor){
        if (isStart) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }
        String path = processor.getPath();
        if(path == null){
            throw new IllegalArgumentException(" getProtoEventId -1 ");
        }
        this.context.getEventDispatcher().registerHttpEvent(path, processor);
    }

    public void registerHttpProcessor(String path, HttpProcessor processor){
        processor.setPath(path);
        this.registerHttpProcessor(processor);
    }

    public void registerSendEventIdByProto(int eventId, Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
        if (isStart) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }

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

    public final synchronized void start() throws InterruptedException{
        if (isStart) {
//            throw new IllegalStateException("server is already start");
            logger.error("server is already start ");
            return;
        }
        isStart = true;
        beforeStart();
        doStart();
    }

    protected void beforeStart() {

    }

    public abstract void doStart() throws InterruptedException;

    public void stop() throws InterruptedException{

    }

}

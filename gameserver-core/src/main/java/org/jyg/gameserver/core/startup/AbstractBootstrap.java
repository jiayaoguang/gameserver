package org.jyg.gameserver.core.startup;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.consumer.RingBufferConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create by jiayaoguang at 2018年3月5日
 * 抽象启动类
 */

public abstract class AbstractBootstrap {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Context context;

    protected volatile boolean isStart = false;

    public AbstractBootstrap() {
        this(new RingBufferConsumer());
    }

    public AbstractBootstrap(Consumer defaultConsumer) {
        this(new Context(defaultConsumer));
    }

    public AbstractBootstrap(Context context) {
        this.context = context;
//        this.defaultConsumer.getEventConsumerFactory().setContext(context);
    }

//    public void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory) {
//        if (isStart) {
//            logger.error("oprete fail,server is already start ");
//            return;
//        }
//        eventConsumerFactory.setEventDispatcher(this.context.getEventDispatcher());
//        this.defaultConsumer.setEventConsumerFactory(eventConsumerFactory);
//
//    }

    public void registerSocketEvent(int msgId, ProtoProcessor<? extends MessageLite> protoProcessor) {
        this.addProtoProcessor(msgId, protoProcessor);
    }

    public void addProtoProcessor(ProtoProcessor<? extends MessageLite> protoProcessor) {
        int msgId = context.getMsgIdByProtoClass(protoProcessor.getProtoClass());
        this.context.getDefaultConsumer().addProcessor(msgId, protoProcessor);
    }

    public void addByteMsgObjProcessor(ByteMsgObjProcessor<? extends ByteMsgObj> byteMsgObjProcessor) {
//        int msgId = byteMsgObjProcessor.getMsgId();
        this.context.getDefaultConsumer().addProcessor(byteMsgObjProcessor);
    }

    public void addProtoProcessor(int msgId,ProtoProcessor<? extends MessageLite> protoProcessor) {
        this.context.getDefaultConsumer().addProcessor(msgId, protoProcessor);
    }

    public void registerHttpEvent(String path, HttpProcessor processor){
        this.addHttpProcessor( path, processor);
    }

    public void addHttpProcessor(HttpProcessor processor) {
        if (isStart) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }
        String path = processor.getPath();
        if (path == null) {
            throw new IllegalArgumentException(" getProtoEventId -1 ");
        }
        this.context.getDefaultConsumer().addHttpProcessor(path, processor);
    }


    public void addHttpProcessor(String path, HttpProcessor processor){
        processor.setPath(path);
        this.addHttpProcessor(processor);
    }

    public void registerSendEventIdByProto(int eventId, Class<? extends MessageLite> protoClazz) throws Exception {
        this.addMsgId2ProtoMapping(eventId, protoClazz);
    }

    public void addMsgId2ProtoMapping(int eventId, Class<? extends MessageLite> protoClazz) throws Exception {
        if (isStart) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }
        this.context.addMsgId2ProtoMapping(eventId, protoClazz);
    }

    public void addMsgId2ProtoMapping(int eventId, MessageLite defaultInstance) throws Exception {
        if (isStart) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }
        this.context.addMsgId2ProtoMapping(eventId, defaultInstance);
    }

    public Logger getLogger() {
        return logger;
    }

    public Consumer getDefaultConsumer() {
        return context.getDefaultConsumer();
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
        context.start();
        beforeStart();
        doStart();

        for(Consumer consumer : context.getConsumerManager().getConsumers()){
            consumer.start();
        }
    }

    protected void beforeStart() {

    }

    public abstract void doStart() throws InterruptedException;

    public void stop() throws InterruptedException{
        for(Consumer consumer : context.getConsumerManager().getConsumers()){
            consumer.stop();
        }
    }

}

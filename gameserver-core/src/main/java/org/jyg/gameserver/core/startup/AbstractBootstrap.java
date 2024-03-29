package org.jyg.gameserver.core.startup;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create by jiayaoguang at 2018年3月5日
 * 抽象启动类
 */

public abstract class AbstractBootstrap implements Lifecycle {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GameContext gameContext;

    protected volatile boolean start = false;

    private volatile boolean stop = false;

    public AbstractBootstrap() {
        this(new MpscQueueGameConsumer());
    }

    public AbstractBootstrap(GameConsumer defaultGameConsumer) {
        this(new GameContext(defaultGameConsumer));
    }

    public AbstractBootstrap(GameContext gameContext) {
        this.gameContext = gameContext;
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
        int msgId = gameContext.getMsgIdByProtoClass(protoProcessor.getProtoClass());
        this.gameContext.getMainGameConsumer().addProcessor(msgId, protoProcessor);
    }

    public void addByteMsgObjProcessor(ByteMsgObjProcessor<? extends ByteMsgObj> byteMsgObjProcessor) {
//        int msgId = byteMsgObjProcessor.getMsgId();
        this.gameContext.getMainGameConsumer().addProcessor(byteMsgObjProcessor);
    }

    public void addProtoProcessor(int msgId,ProtoProcessor<? extends MessageLite> protoProcessor) {
        this.gameContext.getMainGameConsumer().addProcessor(msgId, protoProcessor);
    }


    public void addHttpProcessor(HttpProcessor processor) {
        if (start) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }
        String path = processor.getPath();
        if (path == null) {
            throw new IllegalArgumentException(" getProtoEventId -1 ");
        }
        this.gameContext.getMainGameConsumer().addHttpProcessor(processor);
    }



    public void registerSendEventIdByProto(int eventId, Class<? extends MessageLite> protoClazz) throws Exception {
        this.addMsgId2ProtoMapping(eventId, protoClazz);
    }

    public void addMsgId2ProtoMapping(int eventId, Class<? extends MessageLite> protoClazz)  {
        if (start) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }
        this.gameContext.addMsgId2ProtoMapping(eventId, protoClazz);
    }

    public void addMsgId2ProtoMapping(int eventId, MessageLite defaultInstance)  {
        if (start) {
            throw new IllegalArgumentException(" registerHttpProcessor fail ,server is start ");
        }
        this.gameContext.addMsgId2ProtoMapping(eventId, defaultInstance);
    }

    public Logger getLogger() {
        return logger;
    }

    public GameConsumer getDefaultConsumer() {
        return gameContext.getMainGameConsumer();
    }

    public GameContext getGameContext() {
        return gameContext;
    }




    public final synchronized void start(){


        if (start) {
//            throw new IllegalStateException("server is already start");
            logger.error("server is already start ");
            return;
        }

        beforeStart();

        start = true;

        gameContext.start();

        doStart();


    }

    protected void beforeStart(){

    }



    protected abstract void doStart();

    public final synchronized void stop(){
        if(this.stop){
            return;
        }

        gameContext.stop();
        doStop();
        this.stop = true;
    }


    protected void doStop() {

    }




}

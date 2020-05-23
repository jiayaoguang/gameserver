package org.jyg.gameserver.core.util;

import com.google.protobuf.GeneratedMessageV3;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.consumer.EventConsumerFactory;
import org.jyg.gameserver.core.enums.EventType;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.processor.NotFoundHttpProcessor;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.timer.DelayCloseTimer;
import org.jyg.gameserver.core.timer.TimerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public abstract class IGlobalQueue {

    public static final int DEFAULT_QUEUE_SIZE = 1024 * 64;

    private final HttpProcessor notFOundProcessor = new NotFoundHttpProcessor();

    private Context context;

    public final Map<String, HttpProcessor> httpProcessorMap = new HashMap<>();

    private final TimerManager timerManager = new TimerManager();

    private final Int2ObjectMap<ProtoProcessor<? extends GeneratedMessageV3>> protoProcessorMap = new Int2ObjectOpenHashMap<>();

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract void start();

    public abstract void stop();

    public abstract void publicEvent(EventType evenType, Object data, Channel channel);

    public abstract void publicEvent(EventType evenType, Object data, Channel channel, int eventId);

    public abstract void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory);

    public abstract EventConsumerFactory getEventConsumerFactory();

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void addHttpProcessor(String path, HttpProcessor processor , Context context) {
        if (httpProcessorMap.containsKey(path)) {
            throw new IllegalArgumentException("dupilcated path");
        }
        if (path.charAt(0) != '/' || path.contains(".")) {
            throw new IllegalArgumentException("path cannot contain char:'.' and must start with char:'/' ");
        }

        httpProcessorMap.put(path, processor);
        processor.setContext(context);
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
    public void addProtoProcessor(int msgId, ProtoProcessor<? extends GeneratedMessageV3> processor , Context context) {
        if (protoProcessorMap.containsKey(msgId)) {
            throw new IllegalArgumentException("dupilcated eventid");
        }
        processor.setContext(context);
        protoProcessorMap.put(msgId, processor);
    }
    public ProtoProcessor<? extends GeneratedMessageV3> getProtoProcessor(int msgId) {
        return protoProcessorMap.get(msgId);
    }

    /**
     * 处理普通socket事件
     */
    public void processProtoEvent(LogicEvent<? extends GeneratedMessageV3> event) {
//		MessageLite msg = event.getData();
        ProtoProcessor processor = protoProcessorMap.get(event.getEventId());
        if (processor == null) {
            System.out.println("unknown socket eventid :" + event.getEventId());
            return;
        }
        processor.process( event);
    }
    public void processHttpEvent(LogicEvent<Request> event) {
        getHttpProcessor(event.getData().noParamUri()).process(event);
        //六十秒后关闭
        timerManager.addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
    }
}

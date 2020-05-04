package com.jyg.util;

import com.jyg.bean.LogicEvent;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.enums.EventType;
import io.netty.channel.Channel;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * create by jiayaoguang on 2020/5/1
 * 暂时不支持
 */
@Deprecated
public class BlockingGlobalQueue implements IGlobalQueue {

    private final Queue<Object> queue;

    public BlockingGlobalQueue() {
        this(DEFAULT_QUEUE_SIZE);
    }

    public BlockingGlobalQueue(int size) {
        this.queue = new ArrayBlockingQueue<>(size);
    }

    public BlockingGlobalQueue(Queue<Object> queue) {
        this.queue = queue;
    }

    @Override
    public void start() {
        //do nothing
    }

    @Override
    public void stop() {
        //do nothing
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel) {
        this.publicEvent(evenType,data,channel,0);
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId) {

        LogicEvent<Object> logicEvent = new LogicEvent<>();
        logicEvent.setChannel(channel);
        logicEvent.setChannelEventType(evenType);
        logicEvent.setData(data);
        logicEvent.setEventId(eventId);

        this.queue.add(logicEvent);
    }

    @Override
    public void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public EventConsumerFactory getEventConsumerFactory() {
        return null;
    }
}

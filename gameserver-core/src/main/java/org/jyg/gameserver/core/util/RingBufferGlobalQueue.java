package org.jyg.gameserver.core.util;

import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.consumer.DefaultEventConsumerFactory;
import org.jyg.gameserver.core.consumer.EventConsumer;
import org.jyg.gameserver.core.consumer.EventConsumerFactory;
import org.jyg.gameserver.core.enums.EventType;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class RingBufferGlobalQueue extends IGlobalQueue {

    private static Disruptor<LogicEvent<Object>> disruptor;
    private static final int BUFFER_SIZE = 1024 * 64;
    private RingBuffer<LogicEvent<Object>> ringBuffer;
    private EventConsumerFactory eventConsumerFactory;

    private boolean isStart = false;

    public RingBufferGlobalQueue() {
        this(new DefaultEventConsumerFactory());
    }

    public RingBufferGlobalQueue(EventConsumerFactory eventConsumerFactory) {
        this.eventConsumerFactory = eventConsumerFactory;
    }

    @Override
    public synchronized void start() {

        if(this.isStart){
            return;
        }
        this.isStart = true;

        EventFactory<LogicEvent<Object>> eventFactory = LogicEvent::new;

        ThreadFactory consumerThreadFactory = new PrefixNameThreadFactory("ringbuffer_consumer_thread_");

        disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, consumerThreadFactory, ProducerType.MULTI,
                new LoopAndSleepWaitStrategy());
        EventConsumer eventConsumer = this.eventConsumerFactory.createAndInit(getContext());
        disruptor.handleEventsWith(eventConsumer);
        disruptor.setDefaultExceptionHandler(new LogExceptionHandler());
        ringBuffer = disruptor.getRingBuffer();
        disruptor.start();
    }

    @Override
    public void stop() {
        disruptor.shutdown();
    }

    public EventConsumerFactory getEventConsumerFactory() {
        return eventConsumerFactory;
    }

    public void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory) {
        this.eventConsumerFactory = eventConsumerFactory;
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel) {
        publicEvent(evenType, data, channel, 0);
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId) {
        long sequence = this.ringBuffer.next();
        try {
            LogicEvent<Object> event = this.ringBuffer.get(sequence);
            event.setData(data);
            event.setChannel(channel);
            event.setEventId(eventId);
            event.setChannelEventType(evenType);
        } finally {
            this.ringBuffer.publish(sequence);
        }
    }


    private static class LogExceptionHandler implements ExceptionHandler<LogicEvent> {

        private static final Logger LOGGER = LoggerFactory.getLogger(LogExceptionHandler.class);

        @Override
        public void handleEventException(Throwable ex, long sequence, LogicEvent event) {
            LOGGER.error("handleEventException seq {}  Throwable {} , eventType  {}  , eventId {}",
                    sequence, ex, event.getChannelEventType(), event.getEventId());
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            LOGGER.error("handleOnStartException  Throwable {}", ex);
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            LOGGER.error("handleOnStartException  Throwable {}", ex);
        }
    }
}

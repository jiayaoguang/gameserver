package com.jyg.util;

import com.jyg.bean.LogicEvent;
import com.jyg.consumer.DefaultEventConsumerFactory;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.enums.EventType;
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
public class RingBufferGlobalQueue implements IGlobalQueue {

    private static Disruptor<LogicEvent<Object>> disruptor;
    private static final int BUFFER_SIZE = 1024 * 64;
    private RingBuffer<LogicEvent<Object>> ringBuffer;
    private final EventConsumerFactory eventConsumerFactory;

    public RingBufferGlobalQueue() {
        this.eventConsumerFactory = new DefaultEventConsumerFactory();
    }

    public RingBufferGlobalQueue(EventConsumerFactory defaultEventConsumerFactory) {
        this.eventConsumerFactory = defaultEventConsumerFactory;
    }

    @Override
    public void start() {
        EventFactory<LogicEvent<Object>> eventFactory = LogicEvent::new;

        ThreadFactory consumerThreadFactory = new PrefixNameThreadFactory("ringbuffer_consumer_thread_");

        disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, consumerThreadFactory, ProducerType.MULTI,
                new LoopAndSleepWaitStrategy());

        disruptor.handleEventsWith(this.eventConsumerFactory.newEventConsumer());
        disruptor.setDefaultExceptionHandler(new LogExceptionHandler());
        ringBuffer = disruptor.getRingBuffer();
        disruptor.start();
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
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

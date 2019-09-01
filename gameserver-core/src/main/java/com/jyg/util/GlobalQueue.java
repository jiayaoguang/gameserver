package com.jyg.util;

import com.jyg.bean.LogicEvent;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.enums.EventType;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * created by jiayaoguang at 2017年12月18日
 */
public class GlobalQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalQueue.class);

    private static Disruptor<LogicEvent<Object>> disruptor;
    private static final int BUFFER_SIZE = 1024 * 64;
    private static RingBuffer<LogicEvent<Object>> ringBuffer;

    public static void start(EventConsumerFactory defaultEventConsumerFactory) {

        EventFactory<LogicEvent<Object>> eventFactory = LogicEvent::new;

        ThreadFactory consumerThreadFactory = new PrefixNameThreadFactory("ringbuffer_consumer_thread_");

        disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, consumerThreadFactory, ProducerType.MULTI,
                new LoopAndSleepWaitStrategy());

        disruptor.handleEventsWith(defaultEventConsumerFactory.newEventConsumer());
        disruptor.setDefaultExceptionHandler(new LogExceptionHandler());
        ringBuffer = disruptor.getRingBuffer();
        disruptor.start();
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

    /**
     * @param groupNum 消费者组数量
     * @param groupNum 组里的消费者数量
     */
    private void createConsumerGroups(int groupNum, int oneGroupConsumerNum, EventConsumerFactory eventConsumerFactory) {
        for (int i = 0; i < groupNum; i++) {
            EventHandlerGroup<LogicEvent<Object>> handleEventsWith = disruptor.handleEventsWith(eventConsumerFactory.newEventConsumer());
            for (int j = 0; j < oneGroupConsumerNum - 1; j++) {
                handleEventsWith.handleEventsWith(eventConsumerFactory.newEventConsumer());
            }
        }
    }

    public static void shutdown() {
        disruptor.shutdown();
    }

    public static void publicEvent(EventType evenType, Object data, Channel channel) {
        publicEvent(evenType, data, channel, 0);
    }

    public static void publicEvent(EventType evenType, Object data, Channel channel, int eventId) {
        long sequence = GlobalQueue.ringBuffer.next();
        try {
            LogicEvent<Object> event = GlobalQueue.ringBuffer.get(sequence);
            event.setData(data);
            event.setChannel(channel);
            event.setEventId(eventId);
            event.setChannelEventType(evenType);
        } finally {
            GlobalQueue.ringBuffer.publish(sequence);
        }
    }

}

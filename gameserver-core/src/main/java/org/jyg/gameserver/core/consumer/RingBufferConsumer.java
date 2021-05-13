package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.enums.EventType;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.LoopAndSleepWaitStrategy;
import org.jyg.gameserver.core.util.PrefixNameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class RingBufferConsumer extends Consumer {

    private static Disruptor<EventData<Object>> disruptor;
    private static final int BUFFER_SIZE = 1024 * 64;
    private RingBuffer<EventData<Object>> ringBuffer;

    private boolean isStart = false;


    public RingBufferConsumer() {

    }



    @Override
    public synchronized void doStart() {
        if(this.isStart){
            return;
        }
        this.isStart = true;

        EventFactory<EventData<Object>> eventFactory = EventData::new;

        ThreadFactory consumerThreadFactory = new PrefixNameThreadFactory("ringbuffer_consumer_thread_");

        disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, consumerThreadFactory, ProducerType.MULTI,
                new LoopAndSleepWaitStrategy(this::update));


        disruptor.handleEventsWith((objectLogicEvent, l, b) -> this.onReciveEvent(objectLogicEvent));

//        disruptor.handleEventsWith(eventConsumer);

        disruptor.setDefaultExceptionHandler(new LogExceptionHandler());
        ringBuffer = disruptor.getRingBuffer();
        disruptor.start();
    }

    @Override
    public void stop() {
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
            EventData<Object> event = this.ringBuffer.get(sequence);
            event.setData(data);
            event.setChannel(channel);
            event.setEventId(eventId);
            event.setChannelEventType(evenType);
        } finally {
            this.ringBuffer.publish(sequence);
        }
    }


    private static class LogExceptionHandler implements ExceptionHandler<EventData> {

        private static final Logger LOGGER = LoggerFactory.getLogger(LogExceptionHandler.class);

        @Override
        public void handleEventException(Throwable ex, long sequence, EventData event) {
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

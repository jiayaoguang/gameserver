package org.jyg.gameserver.core.consumer;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.LoopAndSleepWaitStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * create by jiayaoguang on 2020/5/1
 */
@Deprecated
public class RingBufferGameConsumer extends GameConsumer {

    private static Disruptor<EventData<Object>> disruptor;
    private static final int BUFFER_SIZE = 1024 * 64;
    private RingBuffer<EventData<Object>> ringBuffer;



    public RingBufferGameConsumer() {

    }



    @Override
    public synchronized void doStart() {


        EventFactory<EventData<Object>> eventFactory = EventData::new;

        ThreadFactory consumerThreadFactory = new DefaultThreadFactory(getClass().getSimpleName() + "_" + getId()){
            @Override
            public Thread newThread(Runnable r) {
                return super.newThread(() -> {
                    RingBufferGameConsumer.this.onThreadStart();
                    r.run();
                });
            }
        };

        disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, consumerThreadFactory, ProducerType.MULTI,
                new LoopAndSleepWaitStrategy(this::update));


        disruptor.handleEventsWith((objectLogicEvent, l, b) -> this.onReceiveEvent(objectLogicEvent));

//        disruptor.handleEventsWith(eventConsumer);

        disruptor.setDefaultExceptionHandler(new LogExceptionHandler());
        ringBuffer = disruptor.getRingBuffer();
        disruptor.start();
    }

    @Override
    public void doStop() {
        disruptor.shutdown();
    }



    @Override
    public void publishEvent(EventData<?> eventData) {
        long sequence = this.ringBuffer.next();
        try {
            EventData<Object> event = this.ringBuffer.get(sequence);

            event.setEventId(eventData.getEventId());
//            event.setEventType();
            event.setEvent(eventData.getEvent());
        } finally {
            this.ringBuffer.publish(sequence);
        }
    }


//    @Override
//    public void doEvent(EventData event){
//        EventData eventDataCopy = new EventData(event.getChannel(), event.getEventType(), event.getData(), event.getEventExtData(), event.getEventId());
//        eventDataCopy.setEvent(event.getEvent());
//        super.doEvent(eventDataCopy);
//    }


    private static class LogExceptionHandler implements ExceptionHandler<EventData<?>> {

        private static final Logger LOGGER = LoggerFactory.getLogger(LogExceptionHandler.class);

        @Override
        public void handleEventException(Throwable ex, long sequence, EventData event) {
            LOGGER.error("handleEventException seq {}  Throwable {} , eventType  {}  , eventId {}",
                    sequence, ExceptionUtils.getStackTrace(ex), event.getEvent(), event.getEventId());
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            LOGGER.error("handleOnStartException  Throwable {}", ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException("start make Exception");
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            LOGGER.error("handleOnStartException  Throwable {}", ExceptionUtils.getStackTrace(ex));
        }
    }
}

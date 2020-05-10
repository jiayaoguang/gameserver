package com.jyg.util;

import com.jyg.bean.LogicEvent;
import com.jyg.consumer.DefaultEventConsumerFactory;
import com.jyg.consumer.EventConsumer;
import com.jyg.consumer.EventConsumerFactory;
import com.jyg.enums.EventType;
import io.netty.channel.Channel;

import java.util.concurrent.*;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class BlockingGlobalQueue implements IGlobalQueue {

    private final BlockingQueue<LogicEvent<Object>> queue;

    private final EventConsumerFactory eventConsumerFactory;

    private ConsumerThread consumerThread;

    public BlockingGlobalQueue() {
        this(new LinkedBlockingQueue<>());
    }

    public BlockingGlobalQueue(int size) {
        this(new LinkedBlockingQueue<>(size));
    }

    public BlockingGlobalQueue(BlockingQueue<LogicEvent<Object>> queue) {
        this.queue = queue;
        this.eventConsumerFactory = new DefaultEventConsumerFactory();
    }

    @Override
    public void start() {
        consumerThread = new ConsumerThread(queue , eventConsumerFactory.createAndInit());
        consumerThread.setName("blockingQueue_consumer_thread");
        consumerThread.setDaemon(false);

        consumerThread.start();

    }

    @Override
    public void stop() {
        consumerThread.setStop();
        while (consumerThread.isAlive()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AllUtil.println("stop success....");
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

        this.queue.offer(logicEvent);
    }

    @Override
    public void setEventConsumerFactory(EventConsumerFactory eventConsumerFactory) {
        throw new UnsupportedOperationException("todo");
    }

    @Override
    public EventConsumerFactory getEventConsumerFactory() {
        return eventConsumerFactory;
    }

    private static class ConsumerThread extends Thread{
        private final BlockingQueue<LogicEvent<Object>> queue;
        private final EventConsumer eventConsumer;

        private volatile boolean isStop = false;

        private ConsumerThread(BlockingQueue<LogicEvent<Object>> queue, EventConsumer eventConsumer) {
            this.queue = queue;
            this.eventConsumer = eventConsumer;
        }

        @Override
        public void run() {
            int pollNullNum = 0;
            for (;!isStop;){

                LogicEvent<Object> object = queue.poll();
                if(object == null){
                    pollNullNum ++;
                    if(pollNullNum > 1000){
                        if(pollNullNum%100 == 0){
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    continue;
                }


                pollNullNum = 0;
                try {
                    eventConsumer.onEvent(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            AllUtil.println(" stop.... ");
        }

        public void setStop(){
            isStop = true;
        }
    }
}

package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.enums.EventType;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class BlockingConsumer extends Consumer {

    private final BlockingQueue<EventData<Object>> queue;

    private ConsumerThread consumerThread;

    public BlockingConsumer() {
        this(new LinkedBlockingQueue<>());
    }

    public BlockingConsumer(int size) {
        this(new LinkedBlockingQueue<>(size));
    }

    public BlockingConsumer(BlockingQueue<EventData<Object>> queue) {
        this.queue = queue;
    }

    @Override
    public void doStart() {

        consumerThread = new ConsumerThread(queue, this);
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
        Logs.DEFAULT_LOGGER.info("stop success....");
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel) {
        this.publicEvent(evenType,data,channel,0);
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId) {

        EventData<Object> logicEvent = new EventData<>();
        logicEvent.setChannel(channel);
        logicEvent.setChannelEventType(evenType);
        logicEvent.setData(data);
        logicEvent.setEventId(eventId);

        this.queue.offer(logicEvent);
    }



    private static class ConsumerThread extends Thread{
        private final BlockingQueue<EventData<Object>> queue;
        private final Consumer consumer;

        private volatile boolean isStop = false;

        private ConsumerThread(BlockingQueue<EventData<Object>> queue, Consumer consumer) {
            this.queue = queue;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            int pollNullNum = 0;
            for (;!isStop;){

                EventData<Object> object = queue.poll();
                if(object == null) {
                    pollNullNum++;

                    if (pollNullNum > 1000) {
                        consumer.update();
                        LockSupport.parkNanos(1000 * 1000L);
                    } else if (pollNullNum > 200) {
                        Thread.yield();
                    }

                    continue;
                }


                pollNullNum = 0;
                try {
                    consumer.onReciveEvent(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            Logs.DEFAULT_LOGGER.info(" stop.... ");
        }

        public void setStop(){
            isStop = true;
        }
    }
}

package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.enums.EventType;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.AllUtil;

import java.util.concurrent.*;

/**
 * create by jiayaoguang on 2020/5/1
 */
public class BlockingConsumer extends Consumer {

    private final BlockingQueue<LogicEvent<Object>> queue;

    private ConsumerThread consumerThread;

    public BlockingConsumer() {
        this(new LinkedBlockingQueue<>());
    }

    public BlockingConsumer(int size) {
        this(new LinkedBlockingQueue<>(size));
    }

    public BlockingConsumer(BlockingQueue<LogicEvent<Object>> queue) {
        this.queue = queue;
        this.eventConsumerFactory = new DefaultConsumerHandlerFactory();
    }

    @Override
    public void start() {
        ConsumerHandler eventConsumer = this.eventConsumerFactory.createAndInit(getContext());
        eventConsumer.setTimerManager(timerManager);
        consumerThread = new ConsumerThread(queue , eventConsumer);
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
    public void setEventConsumerFactory(ConsumerHandlerFactory eventConsumerFactory) {
        throw new UnsupportedOperationException("todo");
    }


    private static class ConsumerThread extends Thread{
        private final BlockingQueue<LogicEvent<Object>> queue;
        private final ConsumerHandler eventConsumer;

        private volatile boolean isStop = false;

        private ConsumerThread(BlockingQueue<LogicEvent<Object>> queue, ConsumerHandler eventConsumer) {
            this.queue = queue;
            this.eventConsumer = eventConsumer;
        }

        @Override
        public void run() {
            int pollNullNum = 0;
            for (;!isStop;){

                LogicEvent<Object> object = queue.poll();
                if(object == null) {
                    pollNullNum++;
                    if (pollNullNum > 1000) {
                        if (pollNullNum % 100 == 0) {
                            try {
                                Thread.sleep(10L);
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

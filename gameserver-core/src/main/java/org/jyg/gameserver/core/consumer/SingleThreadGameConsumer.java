package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.util.Logs;

import java.util.Queue;
import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2020/5/1
 */
public abstract class SingleThreadGameConsumer extends GameConsumer {

    private Thread consumerThread;

    private volatile boolean isStop = false;



    @Override
    public void doStart() {


        consumerThread = createConsumerThread();
        consumerThread.start();

    }


    protected Thread createConsumerThread(){
        Thread consumerThread = new Thread(this::run);
        consumerThread.setName(getClass().getSimpleName() + "_" + getId());
        consumerThread.setDaemon(false);
        return consumerThread;
    }



    @Override
    public void doStop() {


        isStop = true;
        if(consumerThread != null){
            for (int i = 0; consumerThread.isAlive(); i++) {
                if(i > 1000){
                    Logs.DEFAULT_LOGGER.error( "consumer {} consumerThread stop fail " , getId());
                    break;
                }

                LockSupport.parkNanos(10_1000_1000L);
            }
        }
        Logs.DEFAULT_LOGGER.info("stop gameConsumer {} id {} success....", this.getClass().getSimpleName() ,getId() );
    }

//    @Override
//    public void publicEvent(EventType evenType, Object data, Channel channel) {
//        this.publicEvent(evenType,data,channel,0);
//    }

//    @Override
//    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId , EventExtData eventExtData) {
//
//        EventData<Object> event = new EventData<>();
//        event.setChannel(channel);
//        event.setEventType(evenType);
//        event.setData(data);
//        event.setEventId(eventId);
//        event.setEventExtData(eventExtData);
//
//        publicEvent(event);
//    }


    protected abstract EventData<?> pollEvent();

    @Override
    public abstract void publicEvent(EventData<?> eventData);


    protected void run() {

        onThreadStart();

        int pollNullNum = 0;
        for (;!isStop;){

            EventData<?> object = pollEvent();
            if(object == null) {
                pollNullNum++;

                if (pollNullNum > 1000) {
                    update();
                    pollNullNum = 0;
                    LockSupport.parkNanos(1000 * 1000L);
                } else if (pollNullNum > 800) {
                    Thread.yield();
                }

                continue;
            }

            pollNullNum = 0;
            try {
                onReciveEvent(object);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Logs.DEFAULT_LOGGER.info(" stop.... ");
    }


    public boolean isStop() {
        return isStop;
    }
}

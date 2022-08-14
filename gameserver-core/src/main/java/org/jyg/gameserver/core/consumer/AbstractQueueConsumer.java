package org.jyg.gameserver.core.consumer;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.Logs;

import java.util.concurrent.locks.LockSupport;

/**
 * create by jiayaoguang on 2020/5/1
 */
public abstract class AbstractQueueConsumer extends Consumer {

    private Thread consumerThread;

    private volatile boolean isStop = false;


    @Override
    public void doStart() {


        consumerThread = new Thread(this::run);
        consumerThread.setName(getClass().getSimpleName() + "_" + getId());
        consumerThread.setDaemon(false);

        consumerThread.start();



    }



    @Override
    public void doStop() {
        isStop = true;

        for (int i = 0; consumerThread.isAlive(); i++) {
            if(i > 1000){
                Logs.DEFAULT_LOGGER.error( "consumer {} consumerThread stop fail " , getId());
                break;
            }
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logs.DEFAULT_LOGGER.info("stop success....");
    }

//    @Override
//    public void publicEvent(EventType evenType, Object data, Channel channel) {
//        this.publicEvent(evenType,data,channel,0);
//    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId , EventExtData eventExtData) {

        EventData<Object> event = new EventData<>();
        event.setChannel(channel);
        event.setEventType(evenType);
        event.setData(data);
        event.setEventId(eventId);
        event.setEventExtData(eventExtData);

        publicEvent(event);
    }


    protected abstract EventData<Object> pollEvent();


    protected abstract void publicEvent(EventData<Object> eventData);

    private void run() {

        onThreadStart();

        int pollNullNum = 0;
        for (;!isStop;){

            EventData<Object> object = pollEvent();
            if(object == null) {
                pollNullNum++;

                if (pollNullNum > 1000) {
                    update();
                    LockSupport.parkNanos(1000 * 1000L);
                } else if (pollNullNum > 200) {
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


}

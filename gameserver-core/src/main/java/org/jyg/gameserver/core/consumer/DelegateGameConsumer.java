package org.jyg.gameserver.core.consumer;

import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.msg.ConsumerEventDataMsg;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.Queue;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class DelegateGameConsumer extends GameConsumer{
    public static final int DEFAULT_QUEUE_SIZE = 256 * 1024;


    private final Queue<EventData<?>> eventDataQueue = new MpscUnboundedArrayQueue<>(DEFAULT_QUEUE_SIZE);

    public DelegateGameConsumer(int id) {
        this.setId(id);
    }





    @Override
    public void doStart() {


    }

    @Override
    public void doStop() {

    }

    @Override
    public void publicEvent(EventData<?> eventData) {

        eventDataQueue.offer(eventData);

    }


    public boolean tryPollEventAndDeal(){

        EventData<?> eventData = eventDataQueue.poll();
        if(eventData == null){
            return false;
        }
        try{

            onReciveEvent(eventData);
            return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


}

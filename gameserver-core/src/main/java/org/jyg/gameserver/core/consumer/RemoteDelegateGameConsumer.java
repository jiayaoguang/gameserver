package org.jyg.gameserver.core.consumer;

import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
import org.jyg.gameserver.core.msg.ConsumerEventDataMsg;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.Queue;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class RemoteDelegateGameConsumer extends DelegateGameConsumer{
    public static final int DEFAULT_QUEUE_SIZE = 256 * 1024;

    private final TcpClient tcpClient;

    private final RemoteConsumerInfo remoteConsumerInfo;

    private final Queue<EventData<?>> eventDataQueue = new MpscUnboundedArrayQueue<>(DEFAULT_QUEUE_SIZE);

    public RemoteDelegateGameConsumer(GameContext gameContext , RemoteConsumerInfo remoteConsumerInfo) {
        this(gameContext ,gameContext.createTcpClient(remoteConsumerInfo.getIp(),remoteConsumerInfo.getPort()) , remoteConsumerInfo );
    }


    public RemoteDelegateGameConsumer(GameContext gameContext , TcpClient tcpClient, RemoteConsumerInfo remoteConsumerInfo) {
        super(remoteConsumerInfo.getConsumerId());
        this.setGameContext(gameContext);
        this.remoteConsumerInfo = remoteConsumerInfo;
        this.tcpClient = tcpClient;


    }



    @Override
    public void doStart() {

        tcpClient.start();
    }

    @Override
    public void doStop() {
        tcpClient.stop();
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

        if(eventData.getEvent().getFromConsumerId() == getId()){
            try{
                onReciveEvent(eventData);
            }catch (Exception e){
                e.printStackTrace();
            }
            Logs.DEFAULT_LOGGER.info("eventData.getFromConsumerId() == getId()");
            return true;
        }

        try{
            ConsumerEventDataMsg consumerEventDataMsg = new ConsumerEventDataMsg();

            consumerEventDataMsg.setToConsumerId(getId());
            consumerEventDataMsg.setEventId(eventData.getEventId());

            consumerEventDataMsg.setEvent(eventData.getEvent());

            consumerEventDataMsg.setChildChooseId(eventData.getChildChooseId());

//            consumerEventDataMsg.getEvent().setRequestId(eventData.getEvent().getRequestId());
//            consumerEventDataMsg.getEvent().setFromConsumerId(eventData.getEvent().getFromConsumerId());
//            consumerEventDataMsg.getEvent().setParams(eventData.getParams());


            if(!this.tcpClient.isConnectAvailable()){
                Logs.DEFAULT_LOGGER.error("DelegateGameConsumer remote consumer {} ip {} port {} connect unavailable, reconnect" ,remoteConsumerInfo.getConsumerId() , remoteConsumerInfo.getIp(), remoteConsumerInfo.getPort());
                tcpClient.connect();
            }
            if(this.tcpClient.isConnectAvailable()){
                this.tcpClient.write(consumerEventDataMsg);
            }else {
                Logs.DEFAULT_LOGGER.error("DelegateGameConsumer send msg fail, remote consumer {} ip {} port {},reconnect fail" ,remoteConsumerInfo.getConsumerId() , remoteConsumerInfo.getIp() , remoteConsumerInfo.getPort());
            }

            return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public TcpClient getTcpClient() {
        return tcpClient;
    }
}

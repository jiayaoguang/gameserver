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
public class RemoteDelegateGameConsumer extends MpscQueueGameConsumer{
    public static final int DEFAULT_QUEUE_SIZE = 256 * 1024;

    private final TcpClient tcpClient;

    private final RemoteConsumerInfo remoteConsumerInfo;


    public RemoteDelegateGameConsumer(GameContext gameContext, QueueConsumerThread queueConsumerThread , RemoteConsumerInfo remoteConsumerInfo) {
        this(gameContext,queueConsumerThread ,gameContext.createTcpClient(remoteConsumerInfo.getIp(),remoteConsumerInfo.getPort()) , remoteConsumerInfo );
    }



    public RemoteDelegateGameConsumer(GameContext gameContext , QueueConsumerThread queueConsumerThread , TcpClient tcpClient, RemoteConsumerInfo remoteConsumerInfo) {
        setId(remoteConsumerInfo.getConsumerId());
        this.setGameContext(gameContext);
        this.remoteConsumerInfo = remoteConsumerInfo;
        this.tcpClient = tcpClient;
        this.setConsumerThread(queueConsumerThread);
        queueConsumerThread.addQueueConsumer(this);
    }

    @Override
    public void doStart() {
        super.doStart();
        tcpClient.start();
    }

    @Override
    public void doStop() {
        super.doStop();
        tcpClient.stop();
    }


    @Override
    protected void doEvent(EventData eventData) {

        if (eventData.getEvent().getFromConsumerId() == getId()) {
            try {
                onReciveEvent(eventData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logs.DEFAULT_LOGGER.info("eventData.getFromConsumerId() == getId()");
            return;
        }

        ConsumerEventDataMsg consumerEventDataMsg = new ConsumerEventDataMsg();

        consumerEventDataMsg.setToConsumerId(getId());
        consumerEventDataMsg.setEventId(eventData.getEventId());

        consumerEventDataMsg.setEvent(eventData.getEvent());

        consumerEventDataMsg.setChildChooseId(eventData.getChildChooseId());

//            consumerEventDataMsg.getEvent().setRequestId(eventData.getEvent().getRequestId());
//            consumerEventDataMsg.getEvent().setFromConsumerId(eventData.getEvent().getFromConsumerId());
//            consumerEventDataMsg.getEvent().setParams(eventData.getParams());


        if (!this.tcpClient.isConnectAvailable()) {
            Logs.DEFAULT_LOGGER.error("DelegateGameConsumer remote consumer {} ip {} port {} connect unavailable, reconnect", remoteConsumerInfo.getConsumerId(), remoteConsumerInfo.getIp(), remoteConsumerInfo.getPort());
            tcpClient.connect();
        }
        if (this.tcpClient.isConnectAvailable()) {
            this.tcpClient.write(consumerEventDataMsg);
        } else {
            Logs.DEFAULT_LOGGER.error("DelegateGameConsumer send msg fail, remote consumer {} ip {} port {},reconnect fail", remoteConsumerInfo.getConsumerId(), remoteConsumerInfo.getIp(), remoteConsumerInfo.getPort());
        }


    }


    public TcpClient getTcpClient() {
        return tcpClient;
    }
}

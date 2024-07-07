package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.RemoteConsumerInfo;
import org.jyg.gameserver.core.msg.ConsumerEventDataMsg;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class RemoteDelegateGameConsumer extends MpscQueueGameConsumer{

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
        this.setConsumerThreadAndAddToThread(queueConsumerThread);

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
            onReciveEvent(eventData);
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


        wirteMessage(consumerEventDataMsg);


    }


    public TcpClient getTcpClient() {
        return tcpClient;
    }


    public void wirteMessage(Object message){
//        tcpClient.checkConnect();
        if (!this.tcpClient.isConnectAvailable()) {
            Logs.DEFAULT_LOGGER.error("DelegateGameConsumer remote consumer {} ip {} port {} connect unavailable, reconnect", remoteConsumerInfo.getConsumerId(), remoteConsumerInfo.getIp(), remoteConsumerInfo.getPort());
            tcpClient.connect();
        }
//        if(message instanceof MessageLite){
//            tcpClient.write(message);
//        }else if(message instanceof ByteMsgObj){
//            tcpClient.write(message);
//        }else {
//            Logs.DEFAULT_LOGGER.error("write message fail , message type {} error",message.getClass().getSimpleName());
//        }


        if (this.tcpClient.isConnectAvailable()) {
            this.tcpClient.write(message);
        } else {
            Logs.DEFAULT_LOGGER.error("DelegateGameConsumer send msg fail, remote consumer {} ip {} port {},reconnect fail", remoteConsumerInfo.getConsumerId(), remoteConsumerInfo.getIp(), remoteConsumerInfo.getPort());
        }

    }



}

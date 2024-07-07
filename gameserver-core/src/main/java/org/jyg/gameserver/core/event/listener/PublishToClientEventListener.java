package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.Event;
import org.jyg.gameserver.core.event.PublishToClientEvent;
import org.jyg.gameserver.core.msg.ConsumerEventDataMsg;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2024/7/7
 */
public class PublishToClientEventListener implements GameEventListener<PublishToClientEvent>{

    private GameConsumer gameConsumer;

    public PublishToClientEventListener( GameConsumer gameConsumer){
        this.gameConsumer = gameConsumer;
    }

    @Override
    public void onEvent(PublishToClientEvent publishToClientEvent) {
        int targetConsumerId =  publishToClientEvent.getTargetConsumerId();
        Session session = gameConsumer.getChannelManager().getRemoteConsumerSession(targetConsumerId);
        if(session == null){
            Logs.CONSUMER.error("targetConsumer {} not found" , publishToClientEvent.getTargetConsumerId());
            return;
        }

        EventData eventData = publishToClientEvent.getEventData();
        ConsumerEventDataMsg consumerEventDataMsg = new ConsumerEventDataMsg();

        consumerEventDataMsg.setToConsumerId(targetConsumerId);
        consumerEventDataMsg.setEventId(eventData.getEventId());

        consumerEventDataMsg.setEvent(eventData.getEvent());

        consumerEventDataMsg.setChildChooseId(eventData.getChildChooseId());



//            consumerEventDataMsg.getEvent().setRequestId(eventData.getEvent().getRequestId());
//            consumerEventDataMsg.getEvent().setFromConsumerId(eventData.getEvent().getFromConsumerId());
//            consumerEventDataMsg.getEvent().setParams(eventData.getParams());


        session.writeMessage(consumerEventDataMsg);


    }
}

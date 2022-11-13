package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.msg.ConsumerEventDataMsg;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class ConsumerEventDataMsgProcessor extends ByteMsgObjProcessor<ConsumerEventDataMsg>{


    @Override
    public void process(Session session, EventData<ConsumerEventDataMsg> event) {
        ConsumerEventDataMsg eventDataMsg = event.getData();

        EventData<Object> fromRemoteEventData = new EventData<>();
        fromRemoteEventData.setEventId(eventDataMsg.getEventId());
        fromRemoteEventData.setData(eventDataMsg.getData());
        fromRemoteEventData.setEventType(eventDataMsg.getEventType());

        long remoteRequestId = eventDataMsg.getRequestId();



        if(eventDataMsg.getFromConsumerId() == getGameConsumer().getId()){
            if(remoteRequestId > 0){
                Logs.DEFAULT_LOGGER.error("remoteRequestId > 0");
                return;
            }
        }


        if(eventDataMsg.getToConsumerId() == eventDataMsg.getFromConsumerId()){
            Logs.DEFAULT_LOGGER.error("ToConsumerId == FromConsumerId");
            return;
        }


        long  localRequestId = 0;
        if(eventDataMsg.getEventType() != EventType.RESULT_CALL_BACK){
            localRequestId = getGameConsumer().registerCallBackMethod(new ResultHandler() {
                @Override
                public void call(int eventId, Object data) {

                    ConsumerEventDataMsg returnEventDataMsg = new ConsumerEventDataMsg();

                    returnEventDataMsg.setToConsumerId(eventDataMsg.getFromConsumerId());
                    returnEventDataMsg.setFromConsumerId(eventDataMsg.getToConsumerId());
                    returnEventDataMsg.setEventType(EventType.RESULT_CALL_BACK);
                    returnEventDataMsg.setEventId(eventId);
                    returnEventDataMsg.setData(data);
                    returnEventDataMsg.setRequestId(eventDataMsg.getRequestId());

                    session.writeMessage(returnEventDataMsg);

                }
            });
        }else {
            localRequestId = remoteRequestId;
        }

        fromRemoteEventData.setEventExtData(new EventExtData(getGameConsumer().getId(), localRequestId, eventDataMsg.getRequestId(), eventDataMsg.getParams()));

        getContext().getConsumerManager().publicEvent( eventDataMsg.getToConsumerId() ,fromRemoteEventData  );

    }
}

package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.event.ResultReturnEvent;
import org.jyg.gameserver.core.msg.ConsumerEventDataMsg;
import org.jyg.gameserver.core.msg.ConsumerEventDataReturnMsg;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class ConsumerEventDataMsgProcessor extends ByteMsgObjProcessor<ConsumerEventDataMsg>{


    @Override
    public void process(Session session, MsgEvent<ConsumerEventDataMsg> event) {
        ConsumerEventDataMsg eventDataMsg = event.getMsgData();

        EventData<Object> fromRemoteEventData = new EventData<>();
        fromRemoteEventData.setEventId(eventDataMsg.getEventId());

        fromRemoteEventData.setEvent(eventDataMsg.getEvent());

        final long remoteRequestId = eventDataMsg.getEvent().getRequestId();
        final int fromConsumerId = eventDataMsg.getEvent().getFromConsumerId();
        final int toConsumerId = eventDataMsg.getToConsumerId();

        if(fromConsumerId == getGameConsumer().getId()){
            if(remoteRequestId > 0){
                Logs.DEFAULT_LOGGER.error("remoteRequestId > 0");
                return;
            }
        }


        if(eventDataMsg.getToConsumerId() == fromConsumerId){
            Logs.DEFAULT_LOGGER.error("ToConsumerId == FromConsumerId");
            return;
        }


        long  localRequestId = 0;
        if(! (eventDataMsg.getEvent() instanceof ResultReturnEvent)){
            localRequestId = getGameConsumer().registerCallBackMethod(new ResultHandler() {
                @Override
                public void call(int eventId, Object data) {

                    ConsumerEventDataReturnMsg returnEventDataMsg = new ConsumerEventDataReturnMsg();

                    returnEventDataMsg.setToConsumerId(fromConsumerId);
                    returnEventDataMsg.setFromConsumerId(toConsumerId);

                    ResultReturnEvent resultReturnEvent = new ResultReturnEvent(remoteRequestId , eventId , data);

                    resultReturnEvent.setFromConsumerId(eventDataMsg.getToConsumerId());

                    returnEventDataMsg.setResultReturnEvent(resultReturnEvent);

                    session.writeMessage(returnEventDataMsg);

                }
            });
        }else {
            localRequestId = remoteRequestId;
        }


        fromRemoteEventData.setChildChooseId(eventDataMsg.getChildChooseId());
//        fromRemoteEventData.setParams(eventDataMsg.getParams());

        eventDataMsg.getEvent().setRequestId(localRequestId);

        fromRemoteEventData.getEvent().setFromConsumerId(getGameConsumer().getId());


        getContext().getConsumerManager().publicEvent(eventDataMsg.getToConsumerId(), fromRemoteEventData );

    }
}

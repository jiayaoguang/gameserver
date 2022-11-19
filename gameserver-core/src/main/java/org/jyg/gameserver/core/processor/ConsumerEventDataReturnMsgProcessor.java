package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.intercept.NotWhiteIpInterceptor;
import org.jyg.gameserver.core.msg.ConsumerEventDataReturnMsg;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class ConsumerEventDataReturnMsgProcessor extends ByteMsgObjProcessor<ConsumerEventDataReturnMsg>{

    public ConsumerEventDataReturnMsgProcessor(){
        addMsgInterceptor(new NotWhiteIpInterceptor());
    }


    @Override
    public void process(Session session, EventData<ConsumerEventDataReturnMsg> event) {
        ConsumerEventDataReturnMsg eventDataMsg = event.getData();

        EventData<Object> fromRemoteEventReturnData = new EventData<>();
        fromRemoteEventReturnData.setEventId(eventDataMsg.getEventId());
        fromRemoteEventReturnData.setData(eventDataMsg.getData());
        fromRemoteEventReturnData.setEventType(eventDataMsg.getEventType());


        if(eventDataMsg.getToConsumerId() == eventDataMsg.getFromConsumerId()){
            Logs.DEFAULT_LOGGER.error("ConsumerEventDataReturnMsg error , ToConsumerId == FromConsumerId {}",eventDataMsg.getFromConsumerId());
            return;
        }


        long  localRequestId = eventDataMsg.getRequestId();

        fromRemoteEventReturnData.setEventExtData(new EventExtData(getGameConsumer().getId(), localRequestId, eventDataMsg.getRequestId(), eventDataMsg.getParams()));

        getContext().getConsumerManager().publicEvent( eventDataMsg.getToConsumerId() ,fromRemoteEventReturnData  );

    }
}

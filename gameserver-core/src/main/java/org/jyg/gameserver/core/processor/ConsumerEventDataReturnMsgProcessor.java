package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.intercept.WhiteIpInterceptor;
import org.jyg.gameserver.core.msg.ConsumerEventDataReturnMsg;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class ConsumerEventDataReturnMsgProcessor extends ByteMsgObjProcessor<ConsumerEventDataReturnMsg>{

    public ConsumerEventDataReturnMsgProcessor(){
        addMsgInterceptor(new WhiteIpInterceptor());
    }


    @Override
    public void process(Session session, MsgEvent<ConsumerEventDataReturnMsg> event) {
        ConsumerEventDataReturnMsg eventDataMsg = event.getMsgData();

//        EventData<Object> fromRemoteEventReturnData = new EventData<>();
//        fromRemoteEventReturnData.setEventId(eventDataMsg.getResultReturnEvent().getEventId());
//        fromRemoteEventReturnData.setEvent(eventDataMsg.getResultReturnEvent());


        if(eventDataMsg.getToConsumerId() == eventDataMsg.getFromConsumerId()){
            Logs.DEFAULT_LOGGER.error("ConsumerEventDataReturnMsg error , ToConsumerId == FromConsumerId {}",eventDataMsg.getFromConsumerId());
            return;
        }


//        long  localRequestId = eventDataMsg.getResultReturnEvent().getRequestId();

//        fromRemoteEventReturnData.setEventExtData(new EventExtData(getGameConsumer().getId(), localRequestId, eventDataMsg.getChildChooseId(), eventDataMsg.getParams()));

        getContext().getConsumerManager().publicEvent( eventDataMsg.getToConsumerId() ,eventDataMsg.getResultReturnEvent()  );

    }
}

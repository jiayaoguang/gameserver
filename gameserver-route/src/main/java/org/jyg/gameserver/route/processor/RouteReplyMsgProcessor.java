package org.jyg.gameserver.route.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.manager.RouteManager;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.msg.DefaultMsg;
import org.jyg.gameserver.core.msg.route.RouteReplyMsg;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;

public class RouteReplyMsgProcessor extends ByteMsgObjProcessor<RouteReplyMsg> {
    public RouteReplyMsgProcessor() {
        super(RouteReplyMsg.class);
    }

    @Override
    public void process(Session session, EventData<RouteReplyMsg> event) {

        Session clientSession = this.getConsumer().getChannelManager().getSession(event.getData().getSessionId());

        int msgId = event.getData().getMsgId();

        AbstractMsgCodec msgCodec = getContext().getMsgCodec(msgId);


        if(msgCodec == null){
            DefaultMsg defaultMsg = new DefaultMsg();
            defaultMsg.setMsgId(msgId);
            defaultMsg.setMsgData(event.getData().getData());
            clientSession.writeMessage(defaultMsg);

            return;
        }

        try {
            Object msgObj =  msgCodec.decode(event.getData().getData());

            EventData eventData = new EventData();
            eventData.setData(msgObj);

            getConsumer().processEventMsg( clientSession , eventData );
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }
}

package org.jyg.gameserver.route.processor;

import org.jyg.gameserver.core.event.InnerMsgEvent;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.DefaultMsg;
import org.jyg.gameserver.core.msg.route.RouteReplyMsg;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;

public class RouteReplyMsgProcessor extends ByteMsgObjProcessor<RouteReplyMsg> {
    public RouteReplyMsgProcessor() {
        super(RouteReplyMsg.class);
    }

    @Override
    public void process(Session session, MsgEvent<RouteReplyMsg> event) {

        Session clientSession = this.getGameConsumer().getChannelManager().getSession(event.getMsgData().getSessionId());

        int msgId = event.getMsgData().getMsgId();

        AbstractMsgCodec msgCodec = getContext().getMsgCodec(msgId);


        if(msgCodec == null){
            DefaultMsg defaultMsg = new DefaultMsg();
            defaultMsg.setMsgId(msgId);
            defaultMsg.setMsgData(event.getMsgData().getData());
            clientSession.writeMessage(defaultMsg);

            return;
        }

        try {
            Object msgObj =  msgCodec.decode(event.getMsgData().getData());


            InnerMsgEvent innerMsgEvent = new InnerMsgEvent(msgId , msgObj);

            getGameConsumer().processEventMsg(clientSession, innerMsgEvent);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }
}

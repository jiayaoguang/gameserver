package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.manager.RouteManager;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.route.RouteMsg;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2022/8/7
 */
public class RouteMsgProcessor extends ByteMsgObjProcessor<RouteMsg> {
    public RouteMsgProcessor() {
    }

    @Override
    public void process(Session session, EventData<RouteMsg> event) {


        Session routeSession = this.getGameConsumer().getInstance(RouteManager.class).getRouteSession(session.getSessionId() , event.getData().getSessionId());

        int msgId = event.getData().getMsgId();

        AbstractMsgCodec msgCodec = getContext().getMsgCodec(msgId);
        try {
            Object msgObj =  msgCodec.decode(event.getData().getData());

            EventData eventData = new EventData();
            eventData.setData(msgObj);
            eventData.setEventId(msgId);

            getGameConsumer().processEventMsg( routeSession , eventData );
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }
}

package org.jyg.gameserver.route.processor;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.route.RouteMsg;
import org.jyg.gameserver.core.processor.AbstractProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.core.util.UnknownMsgHandler;
import org.jyg.gameserver.route.RemoteServerManager;

/**
 * create by jiayaoguang on 2022/9/10
 */
public class RouteMsgToGameMsgProcessor extends AbstractProcessor<Object> {



    public RouteMsgToGameMsgProcessor() {
    }

    @Override
    public void process(Session session, EventData<Object> event) {


        Object msgData = event.getData();
        int msgId = event.getEventId();


        AbstractMsgCodec msgCodec = getContext().getMsgCodec(msgId);
        if(msgCodec == null){
            Logs.DEFAULT_LOGGER.error("msg {} codec not found route msg fail" , msgId);
            return;
        }

        byte[] msgBytes;
        try {
            msgBytes = msgCodec.encode(msgData);
        } catch (Exception e) {
            String exceptionMsg = ExceptionUtils.getStackTrace(e);
            Logs.DEFAULT_LOGGER.error("encode msg {} make Exception {}" , msgId , exceptionMsg);
            return;
        }

        RouteMsg routeMsg = new RouteMsg();
        routeMsg.setMsgId(msgId);
        routeMsg.setData(msgBytes);
        routeMsg.setSessionId(session.getSessionId());

        getContext().getInstance(RemoteServerManager.class).sendRemoteMsg(routeMsg);


    }
}

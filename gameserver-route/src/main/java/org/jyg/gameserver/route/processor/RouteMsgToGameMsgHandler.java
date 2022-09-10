package org.jyg.gameserver.route.processor;

import org.jyg.gameserver.core.msg.route.RouteMsg;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.UnknownMsgHandler;
import org.jyg.gameserver.route.RemoteServerManager;

/**
 * create by jiayaoguang on 2022/9/10
 */
public class RouteMsgToGameMsgHandler implements UnknownMsgHandler {


    private final GameContext gameContext;

    public RouteMsgToGameMsgHandler(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void process(Session session, int msgId, byte[] msgData) {

        RouteMsg routeMsg = new RouteMsg();
        routeMsg.setMsgId(msgId);
        routeMsg.setData(msgData);
        routeMsg.setSessionId(session.getSessionId());

        gameContext.getInstance(RemoteServerManager.class).sendRemoteMsg(routeMsg);
    }
}

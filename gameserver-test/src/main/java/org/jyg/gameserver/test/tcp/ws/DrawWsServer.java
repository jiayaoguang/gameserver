package org.jyg.gameserver.test.tcp.ws;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;

/**
 * create by jiayaoguang on 2020/7/12
 * websocket 画图
 * 127.0.0.1:8080/ws.html
 */
public class DrawWsServer {

    public static void main(String[] args) {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();

        gameServerBootstrap.getGameContext().getMainGameConsumer().addProcessor(new ByteMsgObjProcessor<DrawMsg>(DrawMsg.class) {
            @Override
            public void process(Session session, MsgEvent<DrawMsg> data) {
                for (Session otherSession : getGameConsumer().getChannelManager().getSessions()) {
                    otherSession.writeMessage(data.getMsgData());
                }

            }

        });

        gameServerBootstrap.getGameContext().addMsgId2JsonMsgClassMapping(200,DrawMsg.class);

        gameServerBootstrap.addHttpConnector(8080);
        gameServerBootstrap.addWebSocketConnector(9998);


        gameServerBootstrap.start();



    }

}

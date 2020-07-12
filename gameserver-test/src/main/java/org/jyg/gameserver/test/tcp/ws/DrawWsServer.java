package org.jyg.gameserver.test.tcp.ws;

import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.processor.TextProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;

/**
 * create by jiayaoguang on 2020/7/12
 * websocket 画图
 */
public class DrawWsServer {

    public static void main(String[] args) {

        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();

        gameServerBootstrap.getContext().getDefaultConsumer().setTextProcessor(new TextProcessor() {
            public void process(Session session, LogicEvent<String> event) {

                for (Session otherSession : getDefaultConsumer().getChannelManager().getSessions()) {
                    otherSession.writeWsMessage(event.getData());
                }

            }
        });

        gameServerBootstrap.addWebSocketService(9998);


        try {
            gameServerBootstrap.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}

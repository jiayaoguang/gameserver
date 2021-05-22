package org.jyg.gameserver.test.tcp.ws;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.TextProcessor;
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

        gameServerBootstrap.getContext().getDefaultConsumer().setTextProcessor(new TextProcessor() {
            public void process(Session session, EventData<String> event) {

                for (Session otherSession : getConsumer().getChannelManager().getSessions()) {
                    otherSession.writeWsMessage(event.getData());
                }

            }
        });

        gameServerBootstrap.addHttpConnector(8080);
        gameServerBootstrap.addWebSocketConnector(9998);


        gameServerBootstrap.start();



    }

}

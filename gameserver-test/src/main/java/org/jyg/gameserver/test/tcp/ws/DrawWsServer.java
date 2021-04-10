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

                for (Session otherSession : getDefaultConsumer().getChannelManager().getSessions()) {
                    otherSession.writeWsMessage(event.getData());
                }

            }
        });

        gameServerBootstrap.addHttpService(8080);
        gameServerBootstrap.addWebSocketService(9998);


        try {
            gameServerBootstrap.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}

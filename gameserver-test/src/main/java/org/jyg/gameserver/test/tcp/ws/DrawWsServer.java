package org.jyg.gameserver.test.tcp.ws;

import org.apache.commons.lang3.math.NumberUtils;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.msg.AbstractByteMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
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

        gameServerBootstrap.getGameContext().addMsgId2MsgClassMapping(200,DrawMsg.class);
        gameServerBootstrap.getGameContext().addByteMsgCodec(new AbstractByteMsgCodec<DrawMsg>(DrawMsg.class) {
            @Override
            public byte[] encode(DrawMsg jsonMsg) throws Exception {
                return new String(jsonMsg.getX() + ","+jsonMsg.getY()).getBytes();
            }

            @Override
            public DrawMsg decode(byte[] bytes) throws Exception {
                DrawMsg drawMsg = new DrawMsg();
                String[] xyStr = new String(bytes).split(",");
                drawMsg.setX(NumberUtils.toInt(xyStr[0]));
                drawMsg.setY(NumberUtils.toInt(xyStr[1]));
                return drawMsg;
            }
        });

        gameServerBootstrap.getGameContext().getMainGameConsumer().addProcessor(new ByteMsgObjProcessor<DrawMsg>(DrawMsg.class) {
            @Override
            public void process(Session session, MsgEvent<DrawMsg> data) {
                for (Session otherSession : getGameConsumer().getChannelManager().getSessions()) {
                    otherSession.writeMessage(data.getMsgData());
                }

            }

        });



        gameServerBootstrap.addHttpConnector(8080);
        gameServerBootstrap.addWebSocketConnector(9998);


        gameServerBootstrap.start();



    }

}

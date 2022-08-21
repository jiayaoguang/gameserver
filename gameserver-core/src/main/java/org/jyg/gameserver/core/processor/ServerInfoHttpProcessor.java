package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;

/**
 * create by jiayaoguang at 2021/8/14
 */
public class ServerInfoHttpProcessor extends HttpProcessor {

    public ServerInfoHttpProcessor() {
        super("/serverInfo");
    }

    @Override
    public void service(Request request, Response response) {

        StringBuilder sendMsgSb = new StringBuilder();

        sendMsgSb.append("<h2><center>server info</center></h2>");

        sendMsgSb.append("<pre>");

        sendMsgSb.append("连接数 :").append(getGameConsumer().getChannelManager().getChannelCount());




        sendMsgSb.append("</pre>");


        response.writeAndFlush(sendMsgSb.toString());

    }
}

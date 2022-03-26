package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;

import java.lang.management.*;
import java.util.List;

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

        sendMsgSb.append("连接数 :").append(getConsumer().getChannelManager().getChannelCount());




        sendMsgSb.append("</pre>");


        response.writeAndFlush(sendMsgSb.toString());

    }
}

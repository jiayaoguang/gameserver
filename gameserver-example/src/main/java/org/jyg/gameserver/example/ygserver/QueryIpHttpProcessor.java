package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;

/**
 * create by jiayaoguang on 2022/5/19
 */
public class QueryIpHttpProcessor extends HttpProcessor {

    public QueryIpHttpProcessor(){
        super("/queryIp");
    }

    @Override
    public void service(Request request, Response response) {



        response.writeAndFlush(response.getChannel().remoteAddress().toString());

    }
}

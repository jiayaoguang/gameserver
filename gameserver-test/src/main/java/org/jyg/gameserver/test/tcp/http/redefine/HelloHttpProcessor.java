package org.jyg.gameserver.test.tcp.http.redefine;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;

/**
 * create by jiayaoguang on 2021/8/14
 */
public class HelloHttpProcessor extends HttpProcessor {

    public HelloHttpProcessor() {
        super("/hello");
    }

    @Override
    public void service(Request request, Response response) {
        response.writeAndFlush("hello yg.......");
    }
}

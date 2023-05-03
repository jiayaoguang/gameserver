package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;

/**
 * create by jiayaoguang on 2023/5/3
 */
public class DisableAccessHttpEvent extends Event {


    private final Request request;
    private final Response response;

    public DisableAccessHttpEvent(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }


    public Response getResponse() {
        return response;
    }


}

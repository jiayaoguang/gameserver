package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2023/4/15
 */
public class ForbidAccessHttpEvent extends Event {


    private final Request request;
    private final Response response;

    public ForbidAccessHttpEvent(Request request, Response response) {
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

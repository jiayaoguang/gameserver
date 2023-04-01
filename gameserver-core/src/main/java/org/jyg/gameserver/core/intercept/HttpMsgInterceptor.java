package org.jyg.gameserver.core.intercept;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.HttpRequestEvent;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/8/8
 */
public abstract class HttpMsgInterceptor implements MsgInterceptor<Request> {

    @Override
    public final boolean intercept(Session session , MsgEvent<Request> msgEvent){
        if(msgEvent instanceof HttpRequestEvent){
            return filter((HttpRequestEvent) msgEvent);
        }
        return false;
    }


    public abstract boolean filter(HttpRequestEvent httpRequestEvent);

}

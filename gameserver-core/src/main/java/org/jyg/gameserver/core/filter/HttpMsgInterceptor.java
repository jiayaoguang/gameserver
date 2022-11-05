package org.jyg.gameserver.core.filter;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/8/8
 */
public abstract class HttpMsgInterceptor implements MsgInterceptor<Request> {

    public final boolean filter(Session session , EventData<Request> eventData){
        return filter(eventData);
    }


    public abstract boolean filter(EventData<Request> eventData);

}

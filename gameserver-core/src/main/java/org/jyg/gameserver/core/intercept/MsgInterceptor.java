package org.jyg.gameserver.core.intercept;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/7/31
 */
public interface MsgInterceptor<T>{

    /**
     * @param session
     * @param eventData
     * @return
     */
    public boolean checkAccess(Session session , MsgEvent<T> eventData);

    public boolean checkAccessHttp(Request request , Response response);


    void addWhiteIp(String whiteIp);

}


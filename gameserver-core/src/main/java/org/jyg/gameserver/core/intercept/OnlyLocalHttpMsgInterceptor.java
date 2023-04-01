package org.jyg.gameserver.core.intercept;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AllUtil;

/**
 * create by jiayaoguang on 2021/8/8
 */
public class OnlyLocalHttpMsgInterceptor implements MsgInterceptor<Request> {

    public final boolean intercept(Session session, MsgEvent<Request> eventData) {


        String remoteAddr = session.getRemoteAddr();
        if (remoteAddr.startsWith("127.0.0.1")) {
            return true;
        }
        return false;
    }

}

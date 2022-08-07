package org.jyg.gameserver.core.filter;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AllUtil;

/**
 * create by jiayaoguang on 2021/8/8
 */
public class OnlyLocalHttpMsgFilter implements MsgFilter<Request> {

    public final boolean filter(Session session, EventData<Request> eventData) {
        return filter(eventData);
    }


    public boolean filter(EventData<Request> eventData) {

        String remoteAddr = AllUtil.getChannelRemoteAddr(eventData.getChannel());
        if (remoteAddr.startsWith("127.0.0.1")) {
            return true;
        }
        return false;
    }

}
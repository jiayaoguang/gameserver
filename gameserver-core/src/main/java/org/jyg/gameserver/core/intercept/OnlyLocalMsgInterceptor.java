package org.jyg.gameserver.core.intercept;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/8/8
 */
public class OnlyLocalMsgInterceptor implements MsgInterceptor<Request> {

    public final boolean checkAccess(Session session, MsgEvent<Request> eventData) {


        String remoteAddr = session.getRemoteAddr();
        if (remoteAddr != null && remoteAddr.startsWith("127.0.0.1")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkAccessHttp(Request request, Response response) {
        String ip = response.getIp();
        if ("127.0.0.1".equals(ip)) {
            return true;
        }
        return false;
    }

    @Override
    public void addWhiteIp(String whiteIp) {
        throw new UnsupportedOperationException();
    }

}

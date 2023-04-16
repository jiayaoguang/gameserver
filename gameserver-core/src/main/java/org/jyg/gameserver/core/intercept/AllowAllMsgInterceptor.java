package org.jyg.gameserver.core.intercept;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2021/7/31
 */
public class AllowAllMsgInterceptor implements MsgInterceptor<Object> {

    public static final AllowAllMsgInterceptor ALLOW_ALL_MSG_INTERCEPTOR = new AllowAllMsgInterceptor();

    public static AllowAllMsgInterceptor getInstance(){
        return ALLOW_ALL_MSG_INTERCEPTOR;
    }

    public boolean checkAccess(Session session , MsgEvent<Object> eventData){
        return true;
    }

    public boolean checkAccessHttp(Request request , Response response){
        return true;
    }


    public void addWhiteIp(String whiteIp){
        //do nothing
    }

}


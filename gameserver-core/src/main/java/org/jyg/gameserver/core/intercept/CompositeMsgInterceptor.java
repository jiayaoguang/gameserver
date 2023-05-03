package org.jyg.gameserver.core.intercept;

import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;

/**
 * create by jiayaoguang on 2023/5/3
 */
public class CompositeMsgInterceptor<T> implements MsgInterceptor<T>{

    private MsgInterceptor<T> msgInterceptor1;
    private MsgInterceptor<T>  msgInterceptor2;

    public CompositeMsgInterceptor(MsgInterceptor msgInterceptor1, MsgInterceptor msgInterceptor2) {
        this.msgInterceptor1 = msgInterceptor1;
        this.msgInterceptor2 = msgInterceptor2;
    }

    public boolean checkAccess(Session session , MsgEvent<T> eventData){
        if(msgInterceptor1 != null && !msgInterceptor1.checkAccess(session,eventData)){
            return false;
        }
        if(msgInterceptor2 != null && !msgInterceptor2.checkAccess(session,eventData)){
            return false;
        }
        return true;
    }

    public boolean checkAccessHttp(Request request , Response response){
        if(msgInterceptor1 != null && !msgInterceptor1.checkAccessHttp(request,response)){
            return false;
        }
        if(msgInterceptor2 != null && !msgInterceptor2.checkAccessHttp(request,response)){
            return false;
        }
        return true;
    }


    public void addWhiteIp(String whiteIp){
        if(msgInterceptor1 != null){
            msgInterceptor1.addWhiteIp(whiteIp);
        }
        if(msgInterceptor2 != null){
            msgInterceptor2.addWhiteIp(whiteIp);
        }
    }

}


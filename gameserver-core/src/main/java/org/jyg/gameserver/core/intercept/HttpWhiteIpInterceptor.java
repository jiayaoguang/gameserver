package org.jyg.gameserver.core.intercept;

import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.event.HttpRequestEvent;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.IpUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * create by jiayaoguang on 2021/8/8
 */
public class HttpWhiteIpInterceptor implements MsgInterceptor<Request> {


    private Set<String> whiteIpSet = new HashSet<>();


    public HttpWhiteIpInterceptor() {
        this.whiteIpSet.add("127.0.0.1");
    }

    @Override
    public final boolean checkAccess(Session session , MsgEvent<Request> msgEvent){
        if(msgEvent instanceof HttpRequestEvent){
            return checkAccess((HttpRequestEvent) msgEvent);
        }
        return false;
    }


    public boolean checkAccess(HttpRequestEvent httpRequestEvent){
        String addr = IpUtil.getChannelRemoteIp(httpRequestEvent.getChannel());

        String ip = IpUtil.getIpByAddr(addr);

        if(StringUtils.isEmpty(ip)){
            return false;
        }

        if(whiteIpSet.contains(ip)){
            return true;
        }

        return false;
    }


    public void addWhiteIp(String whiteIp){
        this.whiteIpSet.add(whiteIp);
    }

    public void addWhiteIps(Collection<String> whiteIps){
        this.whiteIpSet.addAll(whiteIps);
    }

}

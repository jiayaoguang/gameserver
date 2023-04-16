package org.jyg.gameserver.core.intercept;

import cn.hutool.core.net.Ipv4Util;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.IpUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * create by jiayaoguang on 2021/8/8
 * TODO 支持子网掩码
 */
public class WhiteIpInterceptor implements MsgInterceptor<Object> {

    private Set<String> whiteIpSet = new HashSet<>();


    public WhiteIpInterceptor() {
        this.whiteIpSet.add("127.0.0.1");
    }

    public final boolean checkAccess(Session session, MsgEvent<Object> eventData) {

        String addr = session.getRemoteAddr();

        String ip = IpUtil.getIpByAddr(addr);

        if(StringUtils.isEmpty(ip)){
            return false;
        }

        if(whiteIpSet.contains(ip)){
            return true;
        }

        return false;
    }

    @Override
    public boolean checkAccessHttp(Request request, Response response) {

        String ip = response.getIp();

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

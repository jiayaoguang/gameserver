package org.jyg.gameserver.core.intercept;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AllUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * create by jiayaoguang on 2021/8/8
 */
public class NotWhiteIpInterceptor implements MsgInterceptor<Object> {

    private Set<String> whiteIpSet = new HashSet<>();


    public NotWhiteIpInterceptor() {
        this.whiteIpSet.add("127.0.0.1");
    }

    public final boolean intercept(Session session, EventData<Object> eventData) {

        String addr = session.getRemoteAddr();


        AllUtil.println(addr);


        return true;
    }




}

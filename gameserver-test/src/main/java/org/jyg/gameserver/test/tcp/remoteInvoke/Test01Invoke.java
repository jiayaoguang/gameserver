package org.jyg.gameserver.test.tcp.remoteInvoke;

import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;
import org.jyg.gameserver.core.util.AllUtil;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/4/24
 */
public class Test01Invoke implements IRemoteInvoke {
    @Override
    public void invoke(Consumer consumer, Map<String,Object> paramMap) {
        AllUtil.println("hello Test01Invoke");
    }
}

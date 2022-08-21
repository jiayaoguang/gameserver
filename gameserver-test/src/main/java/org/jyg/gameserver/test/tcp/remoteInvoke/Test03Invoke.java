package org.jyg.gameserver.test.tcp.remoteInvoke;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;
import org.jyg.gameserver.core.util.AllUtil;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/4/24
 */
public class Test03Invoke implements IRemoteInvoke {
    @Override
    public void invoke(GameConsumer gameConsumer, Map<String,Object> paramMap) {
        AllUtil.println("hello Test03Invoke");
    }
}

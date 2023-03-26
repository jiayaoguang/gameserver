package org.jyg.gameserver.test.tcp.jsonMsg;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;

import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/8
 */
public class AA implements IRemoteInvoke {

    @Override
    public void invoke(GameConsumer gameConsumer, Map<String, Object> paramMap) {
        System.out.println("hello");
    }
}

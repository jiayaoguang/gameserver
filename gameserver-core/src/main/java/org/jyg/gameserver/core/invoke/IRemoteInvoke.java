package org.jyg.gameserver.core.invoke;


import org.jyg.gameserver.core.consumer.GameConsumer;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/4/10
 */
public interface IRemoteInvoke {

    void invoke(GameConsumer gameConsumer, Map<String,Object> paramMap);

}

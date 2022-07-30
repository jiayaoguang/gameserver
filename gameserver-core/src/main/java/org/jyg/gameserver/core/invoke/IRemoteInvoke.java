package org.jyg.gameserver.core.invoke;


import org.jyg.gameserver.core.consumer.Consumer;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/4/10
 */
public interface IRemoteInvoke {

    void invoke(Consumer consumer, Map<String,Object> paramMap);

}

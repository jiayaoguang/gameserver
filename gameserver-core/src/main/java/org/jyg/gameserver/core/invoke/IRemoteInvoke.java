package org.jyg.gameserver.core.invoke;


import java.util.Map;

/**
 * create by jiayaoguang on 2021/4/10
 */
public interface IRemoteInvoke {

    void invoke(Map<String,Object> paramMap);

}

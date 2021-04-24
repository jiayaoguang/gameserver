package org.jyg.gameserver.core.invoke;

import com.alibaba.fastjson.JSONObject;

/**
 * create by jiayaoguang on 2021/4/10
 */
public interface IRemoteInvoke {

    void invoke(JSONObject paramJson);

}

package org.jyg.gameserver.core.util;

import com.alibaba.fastjson.JSONObject;

/**
 * create by jiayaoguang on 2021/4/10
 */
public interface IRemoteInvoke {

    JSONObject invoke(JSONObject paramJson);

}

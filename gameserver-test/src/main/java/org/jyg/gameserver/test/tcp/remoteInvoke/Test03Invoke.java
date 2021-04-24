package org.jyg.gameserver.test.tcp.remoteInvoke;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;
import org.jyg.gameserver.core.util.AllUtil;

/**
 * create by jiayaoguang on 2021/4/24
 */
public class Test03Invoke implements IRemoteInvoke {
    @Override
    public void invoke(JSONObject paramJson) {
        AllUtil.println("hello Test03Invoke");
    }
}

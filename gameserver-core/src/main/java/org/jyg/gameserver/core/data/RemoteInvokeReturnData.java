package org.jyg.gameserver.core.data;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeReturnData implements ByteMsgObj {

    @JSONField(name="1")
    private int invokeId = 0;

    @JSONField(name="2")
    private JSONObject returnJson = null;


    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }

    public JSONObject getReturnJson() {
        return returnJson;
    }

    public void setReturnJson(JSONObject returnJson) {
        this.returnJson = returnJson;
    }
}

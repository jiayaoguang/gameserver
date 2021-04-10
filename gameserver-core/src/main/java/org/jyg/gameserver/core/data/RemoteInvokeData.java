package org.jyg.gameserver.core.data;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.jyg.gameserver.core.msg.ByteMsgObj;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeData implements ByteMsgObj {

    @JSONField(name="1")
    private int consumerId = 0;

    @JSONField(name="2")
    private String invokeName = "";

    @JSONField(name="3")
    private JSONObject paramJson = null;


    @JSONField(name="4")
    private int invokeId = 0;


    public int getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(int consumerId) {
        this.consumerId = consumerId;
    }

    public String getInvokeName() {
        return invokeName;
    }

    public void setInvokeName(String invokeName) {
        this.invokeName = invokeName;
    }

    public JSONObject getParamJson() {
        return paramJson;
    }

    public void setParamJson(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }
}

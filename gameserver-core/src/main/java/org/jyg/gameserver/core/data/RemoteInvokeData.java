package org.jyg.gameserver.core.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeData implements ByteMsgObj {

    @JsonProperty(value="1")
    private int consumerId = 0;

    @JsonProperty(value="2")
    private String invokeName = "";

    @JsonProperty(value="3")
    private Map<String,Object> paramMap = null;


    @JsonProperty(value="4")
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

    public Map<String,Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String,Object> paramMap) {
        this.paramMap = paramMap;
    }

    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }
}

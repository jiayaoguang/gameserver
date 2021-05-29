package org.jyg.gameserver.core.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/4/10
 */
public class RemoteInvokeReturnData implements ByteMsgObj {

    @JsonProperty(value="1")
    private int invokeId = 0;

    @JsonProperty(value="2")
    private Map<String,Object> paramMap = null;


    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
}

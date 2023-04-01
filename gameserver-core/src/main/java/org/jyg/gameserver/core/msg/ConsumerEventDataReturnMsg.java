package org.jyg.gameserver.core.msg;

import org.jyg.gameserver.core.event.ResultReturnEvent;

import java.util.Map;

/**
 * create by jiayaoguang on 2022/11/12
 */
public class ConsumerEventDataReturnMsg implements ByteMsgObj{

    private int toConsumerId;;


    private int fromConsumerId;

    private String childChooseId;


    private ResultReturnEvent resultReturnEvent;

    private Map<String, Object> params;





    public int getFromConsumerId() {
        return fromConsumerId;
    }

    public void setFromConsumerId(int fromConsumerId) {
        this.fromConsumerId = fromConsumerId;
    }


    public String getChildChooseId() {
        return childChooseId;
    }

    public void setChildChooseId(String childChooseId) {
        this.childChooseId = childChooseId;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    public int getToConsumerId() {
        return toConsumerId;
    }

    public void setToConsumerId(int toConsumerId) {
        this.toConsumerId = toConsumerId;
    }


    public ResultReturnEvent getResultReturnEvent() {
        return resultReturnEvent;
    }

    public void setResultReturnEvent(ResultReturnEvent resultReturnEvent) {
        this.resultReturnEvent = resultReturnEvent;
    }
}

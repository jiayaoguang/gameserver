package org.jyg.gameserver.core.data;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class EventExtData {

    public final int fromConsumerId;
    public final long requestId;
    public final String childChooseId;

    public final Map<String, Object> params;


    public EventExtData(int fromConsumerId, long requestId) {
        this(fromConsumerId, requestId, "", null);
    }


    public EventExtData(int fromConsumerId, long requestId, Map<String, Object> params) {
        this(fromConsumerId, requestId, "", params);
    }


    public EventExtData(int fromConsumerId, long requestId,String childChooseId) {
        this(fromConsumerId, requestId, childChooseId, null);
    }

    public EventExtData(int fromConsumerId, long requestId, String childChooseId, Map<String, Object> params) {
        this.fromConsumerId = fromConsumerId;
        this.requestId = requestId;
        this.childChooseId = childChooseId;
        this.params = params;
    }


}

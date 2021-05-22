package org.jyg.gameserver.core.data;

import java.util.Map;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class EventExtData {

    public final int fromConsumerId;
    public final int requestId;
    public final long childChooseId;

    public final Map<String, Object> params;

    public EventExtData(int fromConsumerId, long childChooseId) {
        this(fromConsumerId, 0, childChooseId, null);
    }


    public EventExtData(int fromConsumerId, int requestId, long childChooseId) {
        this(fromConsumerId, requestId, childChooseId, null);
    }

    public EventExtData(int fromConsumerId, int requestId, Map<String, Object> params) {
        this(fromConsumerId, requestId, 0, params);
    }

    public EventExtData(int fromConsumerId, int requestId, long childChooseId, Map<String, Object> params) {
        this.fromConsumerId = fromConsumerId;
        this.requestId = requestId;
        this.childChooseId = childChooseId;
        this.params = params;
    }


}

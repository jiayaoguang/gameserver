package org.jyg.gameserver.core.http;

import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.util.Context;

import java.util.HashMap;

/**
 * create by jiayaoguang on 2022/7/10
 */
public class HttpManager implements Lifecycle {

    private final int httpConsumerId;
    private final Context context;

    public HttpManager(Context context){
        this(HttpConst.DEFAULT_HTTP_CONSUMER_ID , context);
    }

    public HttpManager(int httpConsumerId,Context context){
        this.httpConsumerId = httpConsumerId;
        this.context = context;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public void getAsyn(String url,CallBackEvent callBackEvent){
        context.getConsumerManager().publicEvent(httpConsumerId, EventType.DEFAULT_EVENT,
                new HttpRequestData(HttpConst.REUQEST_TYPE_GET, url, new HashMap<>(), callBackEvent), 0);
    }

}

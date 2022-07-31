package org.jyg.gameserver.core.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.util.Context;

import java.io.IOException;

/**
 * create by jiayaoguang on 2022/7/10
 */
public class HttpCallBack implements Callback {

    private final int fromConsumerId;
    private final Callback callback;
    private final Context context;

    public HttpCallBack(int fromConsumerId, Callback callback, Context context) {
        this.fromConsumerId = fromConsumerId;
        this.callback = callback;
        this.context = context;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        CallBackEvent callBackEvent = new CallBackEvent() {
            @Override
            public void execte() {
                callback.onFailure(call,e);
            }
        };

        context.getConsumerManager().publicEvent(fromConsumerId, EventType.INNER_MSG,
                callBackEvent, 0);

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        CallBackEvent callBackEvent = new CallBackEvent() {
            @Override
            public void execte() {
                try {
                    callback.onResponse(call,response);
                } catch (IOException e) {
                    callback.onFailure(call,e);
                    e.printStackTrace();
                }
            }
        };

        context.getConsumerManager().publicEvent(fromConsumerId, EventType.INNER_MSG,
                callBackEvent, 0);

    }
}

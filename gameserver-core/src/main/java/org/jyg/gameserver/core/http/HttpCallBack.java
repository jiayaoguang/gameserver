package org.jyg.gameserver.core.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.util.GameContext;

import java.io.IOException;

/**
 * create by jiayaoguang on 2022/7/10
 */
public class HttpCallBack implements Callback {

    private final int fromConsumerId;
    private final Callback callback;
    private final GameContext gameContext;

    public HttpCallBack(int fromConsumerId, Callback callback, GameContext gameContext) {
        this.fromConsumerId = fromConsumerId;
        this.callback = callback;
        this.gameContext = gameContext;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        CallBackEvent callBackEvent = new CallBackEvent() {
            @Override
            public void execte() {
                callback.onFailure(call,e);
            }
        };

        gameContext.getConsumerManager().publicEvent(fromConsumerId, EventType.INNER_MSG,
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

        gameContext.getConsumerManager().publicEvent(fromConsumerId, EventType.INNER_MSG,
                callBackEvent, 0);

    }
}

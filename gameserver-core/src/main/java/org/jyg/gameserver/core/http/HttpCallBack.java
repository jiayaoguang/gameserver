package org.jyg.gameserver.core.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jyg.gameserver.core.event.ExecutableEvent;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

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
        ExecutableEvent executableEvent = new ExecutableEvent((()->{
            try {
                callback.onFailure(call,e);
            } catch (Exception exception) {
                Logs.DEFAULT_LOGGER.error("httpClient onFailure Exception", exception);
            }
        }));

        gameContext.getConsumerManager().publishcEvent(fromConsumerId, executableEvent);

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        ExecutableEvent responseExecutableEvent = new ExecutableEvent((()->{
            try (response) {
                callback.onResponse(call, response);
            } catch (Exception e) {
                Logs.DEFAULT_LOGGER.error("httpClient onResponse Exception", e);
            }
        }));


        gameContext.getConsumerManager().publishcEvent(fromConsumerId, responseExecutableEvent);

    }
}

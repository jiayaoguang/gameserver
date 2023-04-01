package org.jyg.gameserver.core.http;

import okhttp3.Callback;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.startup.HttpClient;
import org.jyg.gameserver.core.util.GameContext;

import java.io.IOException;

/**
 * create by jiayaoguang on 2022/7/10
 */
public class GlobalHttpManager implements Lifecycle {

    private final GameContext gameContext;

    private final HttpClient httpClient;

    public GlobalHttpManager(GameContext gameContext){
        this.gameContext = gameContext;
        this.httpClient = new HttpClient();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public void getAsyn(int fromConsumerId , String url, Callback callback){
        HttpCallBack callBackWrapper = new HttpCallBack(fromConsumerId , callback , gameContext);
        try {
            httpClient.getAsyn(url, callBackWrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        context.getConsumerManager().publicEvent(httpConsumerId, EventType.DEFAULT_EVENT,
//                new HttpRequestData(HttpConst.REUQEST_TYPE_GET, url, new HashMap<>(), callBackEvent), 0);
    }

}

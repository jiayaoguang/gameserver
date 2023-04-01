package org.jyg.gameserver.core.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jyg.gameserver.core.consumer.MpscQueueGameConsumer;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
import org.jyg.gameserver.core.event.ExecutableEvent;
import org.jyg.gameserver.core.startup.HttpClient;
import org.jyg.gameserver.core.util.CallBackEvent;

import java.io.IOException;

/**
 * create by jiayaoguang on 2022/7/9
 */
@Deprecated
public class HttpGameConsumer extends MpscQueueGameConsumer {


    private final HttpClient httpClient;

    public HttpGameConsumer() {
        this(HttpConst.DEFAULT_HTTP_CONSUMER_ID);
    }

    public HttpGameConsumer(int id) {
        this(id,new HttpClient());
    }

    public HttpGameConsumer(int id , HttpClient httpClient) {
        this.setId(id);
        this.httpClient = httpClient;
    }



    @Override
    public void processDefaultEvent(ConsumerDefaultEvent eventData) {

        int eventId = eventData.getEventId();


        HttpRequestData httpRequestData = (HttpRequestData) eventData.getData();

        CallBackEvent callBackEvent = httpRequestData.callBackEvent;

        try {
            if (HttpConst.REUQEST_TYPE_POST.equals(httpRequestData.requestType)) {
                httpClient.postAsyn(httpRequestData.getUrl(), httpRequestData.getParams(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBackEvent.setSuccess(false);
                        callBackEvent.setData(e);
                        HttpGameConsumer.this.getGameContext().getConsumerManager().publicEvent(HttpGameConsumer.this.getId(), new ExecutableEvent(callBackEvent::execte));
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        callBackEvent.setSuccess(true);
                        callBackEvent.setData(response);

                        HttpGameConsumer.this.getGameContext().getConsumerManager().publicEvent(HttpGameConsumer.this.getId(), new ExecutableEvent(callBackEvent::execte));
                    }
                });
            } else {
                httpClient.getAsyn(httpRequestData.getUrl(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBackEvent.setSuccess(false);
                        callBackEvent.setData(e);
                        HttpGameConsumer.this.getGameContext().getConsumerManager().publicEvent(HttpGameConsumer.this.getId(), new ExecutableEvent(callBackEvent::execte));
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        callBackEvent.setSuccess(true);
                        callBackEvent.setData(response);

                        HttpGameConsumer.this.getGameContext().getConsumerManager().publicEvent(HttpGameConsumer.this.getId(), new ExecutableEvent(callBackEvent::execte));
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}

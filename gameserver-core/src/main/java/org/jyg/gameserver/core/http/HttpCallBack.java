package org.jyg.gameserver.core.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jyg.gameserver.core.util.CallBackEvent;

import java.io.IOException;

/**
 * create by jiayaoguang on 2022/7/10
 */
public class HttpCallBack implements Callback {


    private final CallBackEvent callBackEvent;

    public HttpCallBack(CallBackEvent callBackEvent) {
        this.callBackEvent = callBackEvent;
    }


    @Override
    public void onFailure(Call call, IOException e) {
        callBackEvent.setSuccess(false);
        callBackEvent.setData(e);

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        callBackEvent.setSuccess(true);
        callBackEvent.setData(response);

    }

}

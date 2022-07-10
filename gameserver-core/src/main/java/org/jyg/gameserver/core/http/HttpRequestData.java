package org.jyg.gameserver.core.http;

import org.jyg.gameserver.core.util.CallBackEvent;

import java.util.Map;

/**
 * create by jiayaoguang on 2022/7/9
 */
public class HttpRequestData {


    public final String requestType;

    public final String url;

    public final Map<String, String> params;

    public final CallBackEvent callBackEvent;

    public HttpRequestData(String reuqestType, String url, Map<String, String> params, CallBackEvent callBackEvent) {
        this.requestType = reuqestType;
        this.url = url;
        this.params = params;
        this.callBackEvent = callBackEvent;
    }


    public String getRequestType() {
        return requestType;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }
}

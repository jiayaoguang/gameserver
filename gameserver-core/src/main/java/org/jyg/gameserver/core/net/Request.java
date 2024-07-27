package org.jyg.gameserver.core.net;

import java.io.IOException;
import java.util.*;

import cn.hutool.json.ObjectMapper;
import com.alibaba.fastjson2.JSONObject;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jyg.gameserver.core.util.Logs;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class Request {

    private long requestid = 0;

    private Map<String, List<String>> parametersMap;


    private String noParamUri;

    private final HttpHeaders headers;

    private final HttpMethod method;

    private boolean isMakeException = false;


    private Map<String,Object> jsonParams = Collections.EMPTY_MAP;


    public Request(HttpRequest httpRequest) {
        headers = httpRequest.headers();
        method = httpRequest.method();

        init(httpRequest);
    }


    public String getParameter(String paramName) {


        List<String> params = getParameterList(paramName);
        if(params == null || params.isEmpty()){
            return null;
        }
        return params.get(0);
    }

    public List<String> getParameterList(String paramName) {
        if(parametersMap == null){
            return Collections.emptyList();
        }
        return parametersMap.get(paramName);
    }

    public String noParamUri() {

        return noParamUri;
    }

    public HttpMethod method() {

        return method;
    }

    public HttpHeaders getHeaders() {

        return headers;
    }

    public Map<String, Object> getJsonParams() {
        return jsonParams;
    }


    public int getJsonIntParam(String param){
        return (Integer) jsonParams.get(param);
    }

    public int getJsonIntParam(String param,int defaultValue){
        Object value = jsonParams.get(param);
        if(value == null){
            return defaultValue;
        }
        return (Integer) value;
    }


    public String getJsonStringParam(String param){
        return (String) jsonParams.get(param);
    }

    public Object getJsonParam(String param){
        return jsonParams.get(param);
    }


    @Deprecated
    private String getNoParamPath(HttpRequest httpRequest) {
//		try {
//			uri = URLDecoder.decode(uri, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			throw new Error(e);
//		}

        String uri = httpRequest.uri();

        if (httpRequest.method() == HttpMethod.GET) {
            int endIndex = uri.indexOf('?');
            if (endIndex == -1) {
                return uri;
            }
            return uri.substring(0, endIndex);
        }

        return uri;
    }

    public Set<Cookie> decodeCookies() {
        String cookiesValue = headers.get(HttpHeaderNames.COOKIE);
        Logs.DEFAULT_LOGGER.info("cookiesValue : " + cookiesValue);
        if (cookiesValue == null) {
            return Collections.emptySet();
        }
        return ServerCookieDecoder.STRICT.decode(cookiesValue);
    }

    public long getRequestid() {
        return requestid;
    }

    public void setRequestid(long requestid) {
        this.requestid = requestid;
    }


    private void init(HttpRequest req) {
        // 处理get请求
        if (req.method() == HttpMethod.GET) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            parametersMap = decoder.parameters();
            noParamUri = decoder.path();
            return;
        }

        // 处理POST请求
        if (req.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                    new DefaultHttpDataFactory(false), req);
            try {
                parametersMap = new HashMap<>();
                while (decoder.hasNext()) {
                    InterfaceHttpData httpData = decoder.next();
                    if (httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) httpData;
                        List<String> params = parametersMap.get(attribute.getName());
                        if (params == null) {
                            params = new ArrayList<>(1);
                            parametersMap.put(attribute.getName(), params);
                        }
                        params.add(attribute.getValue());
                        attribute.release();
                    }
                }
            } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
                //ignore
            } catch (IOException e) {
                e.printStackTrace();
                isMakeException = true;
            } finally {
                decoder.destroy();
            }

        }


        String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
        if(contentType.contains("json")){

            for(String param : parametersMap.keySet() ){
                jsonParams = JSONObject.parseObject(param , HashMap.class);
            }

        }

        noParamUri = req.uri();

    }

    public boolean isMakeException() {
        return isMakeException;
    }
}

package org.jyg.gameserver.core.startup;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jyg.gameserver.core.http.HttpCallBack;

/**
 * created by jiayaoguang at 2018年4月3日
 */
public class HttpClient {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS).build();


    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();

        return request(request);
    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();

        return request(request);
    }

    public void postAsyn( String url,  String json , HttpCallBack callback) throws IOException {


        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        requestAsyn(request, callback);
    }


    public String post(String url, Map<String, String> params) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();

        return request(request);
    }


    //异步，开线程池阻塞等待响应
    public void getAsyn(String url, HttpCallBack callback) throws IOException {
        Request request = new Request.Builder().url(url).build();

        requestAsyn(request, callback);
    }

    public void postAsyn( String url, Map<String, String> params , HttpCallBack callback) throws IOException {

//        RequestBody body = RequestBody.create(JSON, json);

        FormBody.Builder formBuilder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();

        requestAsyn(request, callback);
    }


    public void requestAsyn(Request request, HttpCallBack callback) throws IOException {
        client.newCall(request).enqueue(callback);
    }

    public String request(Request request) throws IOException {
        try(Response response = client.newCall(request).execute();){
            if (response.body() == null) {
                return null;
            }

            return response.body().string();
        }

    }


//	public static void main(String[] args) throws IOException {
//		new HttpClient().getAsyn("https://www.baidu.com");
//	}

}

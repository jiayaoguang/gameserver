package com.jyg.startup;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * created by jiayaoguang at 2018年4月3日
 */
public class HttpClient {

	//TODO 改为异步
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	OkHttpClient client = new OkHttpClient();


	String get(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	String post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	
	
}


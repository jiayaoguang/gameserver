package org.jyg.gameserver.core.startup;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * created by jiayaoguang at 2018年4月3日
 */
public class HttpClient {

	// TODO 改为异步
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	OkHttpClient client = new OkHttpClient.Builder().connectTimeout(50, TimeUnit.SECONDS)
			.readTimeout(50, TimeUnit.SECONDS).writeTimeout(50, TimeUnit.SECONDS).build();
	

	public String get(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public String post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	//异步，开线程池阻塞等待响应
	public void getAsyn(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				System.out.println("fail");
				call.cancel();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				System.out.println("success");
				response.close();
			}
			
		} );
		return ;
	}
	
	public static void main(String[] args) throws IOException {
		new HttpClient().getAsyn("https://www.baidu.com");
	}

}

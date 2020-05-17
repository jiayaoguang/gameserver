package org.jyg.gameserver.auth;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;

/**
 * created by jiayaoguang at 2018年4月3日
 */
public class OkHttpTest {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	@Test
	public void test01() throws IOException {
		String s = new OkHttpTest().post("http://localhost:8080/","{'a':100}");
		System.out.println(s);
	}

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	String post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	
	String asynpost(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		 client.newCall(request).enqueue(new Callback() {

			public void onFailure(Call call, IOException e) {
				// TODO Auto-generated method stub
				
			}

			public void onResponse(Call call, Response response) throws IOException {
				// TODO Auto-generated method stub
				
			}

			
		});
		return "";
	}

}

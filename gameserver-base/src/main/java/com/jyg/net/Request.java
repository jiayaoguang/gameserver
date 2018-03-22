package com.jyg.net;

import java.util.Map;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class Request {

	private Map<String, String> parametersMap;

	private final HttpRequest httpRequest;
	
	public Request(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public void setParametersMap(Map<String, String> parametersMap) {
		this.parametersMap = parametersMap;
	}

	public Map<String, String> getParameters() {
		return parametersMap;
	}

	public String getParameter(String paramName) {
		return parametersMap.get(paramName);
	}

	public String uri() {
		
		return httpRequest.uri();
	}

	public HttpMethod method() {

		return httpRequest.method();
	}
	
	public HttpHeaders getHeaders() {

		return httpRequest.headers();
	}

}

package com.jyg.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class Request {

	private long requestid = 0;
	
	private Map<String, String> parametersMap;

	private final HttpRequest httpRequest;
	
	private final String noParamUri;
	
	
	
	
	public Request(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
		noParamUri = getNoParamPath(httpRequest.uri());
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

	public String noParamUri() {
		
		return noParamUri;
	}

	public HttpMethod method() {

		return httpRequest.method();
	}
	
	public HttpHeaders getHeaders() {

		return httpRequest.headers();
	}
	
	public String getNoParamPath(String uri) {
		try {
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
		
		int endIndex = uri.indexOf('?');
		if(endIndex==-1) {
			return uri;
		}
		
		return uri.substring(0, endIndex);
	}
	
	private static final Set<Cookie> nullSet = new HashSet<>();
	
	public Set<Cookie> decodeCookies() {
		String cookiesValue = httpRequest.headers().get(HttpHeaderNames.COOKIE);
		System.out.println("cookiesValue : " + cookiesValue);
		if(cookiesValue==null) {
			return nullSet;
		}
		return ServerCookieDecoder.STRICT.decode(cookiesValue);
	}

	public long getRequestid() {
		return requestid;
	}

	public void setRequestid(long requestid) {
		this.requestid = requestid;
	}

	
}

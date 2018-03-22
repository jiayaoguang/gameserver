package com.jyg.util;
/**
 * created by jiayaoguang at 2017年12月6日
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MemoryAttribute;


/**
 * HTTP请求参数解析器, 支持GET, POST
 */
public class RequestParser {

    /**
     * 解析请求参数
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     *
     * @throws BaseCheckedException
     * @throws IOException
     */
    public static Map<String, String> parse(HttpRequest req) throws IOException {
    	Map<String, String>requestParams=new HashMap<>();
        // 处理get请求  
        if (req.method() == HttpMethod.GET) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());  
            Map<String, List<String>> param = decoder.parameters();  
            Iterator<Entry<String, List<String>>> iterator = param.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<String, List<String>> next = iterator.next();
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
        }
         // 处理POST请求  
        else if (req.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(  
                    new DefaultHttpDataFactory(false), req);
        	try{
				while (decoder.hasNext()) {
					InterfaceHttpData httpData = decoder.next();
					if (httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
						Attribute attribute = (Attribute) httpData;
						if (!requestParams.containsKey(attribute.getName())) {
							requestParams.put(attribute.getName(), attribute.getValue());
						}
						attribute.release();
					}
	        	}
        	}catch(Exception e){
//        		e.printStackTrace();
        	}
        	
        	decoder.destroy();
        }
        return requestParams;
    }
}
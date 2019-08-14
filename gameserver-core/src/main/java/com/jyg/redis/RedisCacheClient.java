package com.jyg.redis;

/**
 * create on 2019/8/13 by jiayaoguang
 */
public interface RedisCacheClient {

	void init()throws Exception;

	String setValue( String key ,String value )throws Exception;

	String getValue( String key)throws Exception;
}

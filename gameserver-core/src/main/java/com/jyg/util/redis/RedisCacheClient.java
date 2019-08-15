package com.jyg.util.redis;

/**
 * create on 2019/8/13 by jiayaoguang
 */
public interface RedisCacheClient {

	void init()throws Exception;

	String setValue( String key ,String value )throws Exception;

	public String setValueExpire(String key, int expireTimeSecond, String value);

	String getValue( String key)throws Exception;
}

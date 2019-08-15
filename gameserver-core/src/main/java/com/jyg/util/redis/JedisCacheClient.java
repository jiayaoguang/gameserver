package com.jyg.util.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.inject.Inject;

/**
 * create on 2019/8/13 by jiayaoguang
 */
public class JedisCacheClient implements RedisCacheClient {

	private JedisPool pool;

	@Inject
	public JedisCacheClient() {
	}

	public void init() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		pool = new JedisPool(jedisPoolConfig, "localhost",6379);
	}

	@Override
	public String setValue(String key, String value) {
		try(Jedis jedis = pool.getResource()){
			return jedis.set(key,value);
		}
	}

	public String setValueExpire(String key, int expireTimeSecond, String value) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.setex(key, expireTimeSecond, value);
		}
	}

	@Override
	public String getValue(String key) {

		try(Jedis jedis = pool.getResource()){
			return jedis.get(key);
		}
	}

}

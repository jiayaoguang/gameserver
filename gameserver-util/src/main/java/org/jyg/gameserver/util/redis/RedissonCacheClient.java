package org.jyg.gameserver.util.redis;

import org.jyg.gameserver.core.util.RemotingUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import javax.inject.Inject;

/**
 * create on 2019/8/13 by jiayaoguang
 */
public class RedissonCacheClient implements RedisCacheClient {
	@Inject
	public RedissonCacheClient() {
	}

	@Override
	public void init(){

		// 1. Create config object
		Config config = new Config();
		config.useClusterServers()
				// use "rediss://" for SSL connection
				.addNodeAddress("redis://127.0.0.1:7181");
		if(RemotingUtil.isLinuxPlatform()){
			config.setTransportMode(TransportMode.EPOLL);
		}else {
			config.setTransportMode(TransportMode.NIO);
		}

		// 2. Create Redisson instance

// Sync and Async API
		RedissonClient redisson = Redisson.create(config);

// Reactive API
		RedissonReactiveClient redissonReactive = Redisson.createReactive(config);

// RxJava2 API
		RedissonRxClient redissonRx = Redisson.createRx(config);

	}

	@Override
	public String setValue(String key, String value) {
		return key;
	}

	@Override
	public String setValueExpire(String key, int expireTimeSecond, String value) {
		return null;
	}

	@Override
	public String getValue(String key) {
		return null;
	}
}

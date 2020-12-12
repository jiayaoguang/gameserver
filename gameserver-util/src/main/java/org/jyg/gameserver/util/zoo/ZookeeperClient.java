package org.jyg.gameserver.util.zoo;

import com.google.inject.Inject;
import io.netty.util.CharsetUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * create on 2019/8/14 by jiayaoguang
 */
public class ZookeeperClient {
	@Inject
	public ZookeeperClient() {

	}

	private CuratorFramework client;

	public void init() {
		client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
				.sessionTimeoutMs(30000)
				.connectionTimeoutMs(30000)
				.canBeReadOnly(false)
				.retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
				.namespace("yg")
				.defaultData(null)
				.build();
		client.start();
	}

	public void createPath(String path,String data)throws Exception{
		client.create().creatingParentsIfNeeded().forPath(path,data.getBytes());
	}

	public String getPath(String path)throws Exception{
		byte[] bytes = client.getData().forPath(path);
		if(bytes == null){
			return null;
		}
		return new String(bytes, CharsetUtil.UTF_8);
	}


}

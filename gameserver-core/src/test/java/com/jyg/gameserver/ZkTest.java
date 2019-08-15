package com.jyg.gameserver;

import com.jyg.util.zoo.ZookeeperClient;
import org.junit.Test;

/**
 * create on 2019/8/14 by jiayaoguang
 */
public class ZkTest {

	@Test
	public void test(){
		ZookeeperClient zookeeperClient = new ZookeeperClient();
		zookeeperClient.init();
		try {
//			client.create().creatingParentsIfNeeded().forPath("/jyg","hello world".getBytes());
			zookeeperClient.createPath("/jyg02","hello world");

			String data = zookeeperClient.getPath("/jyg02");
			System.out.println("=== str : " + data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("ending ...." );

	}

}

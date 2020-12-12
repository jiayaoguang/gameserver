package org.jyg.gameserver.util;


import org.jyg.gameserver.util.zoo.ZookeeperClient;

/**
 * create on 2019/8/14 by jiayaoguang
 */
public class ZkTest {

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

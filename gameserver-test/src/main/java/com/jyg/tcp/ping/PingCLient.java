package com.jyg.tcp.ping;

import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.proto.p_sm_scene.p_scene_sm_response_pong;
import org.jyg.gameserver.proto.p_sm_scene.p_sm_scene_request_ping;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import io.netty.channel.Channel;

/**
 * Hello world!
 *
 */
public class PingCLient {
	public static void main(String[] args) throws Exception {

		ProtoProcessor<p_scene_sm_response_pong> pongProcessor = new ProtoProcessor<p_scene_sm_response_pong>(
				p_scene_sm_response_pong.getDefaultInstance()) {

			@Override
			public void process(Session session, p_scene_sm_response_pong msg) {
				System.out.println("receive pong msg");
				// response.writeMsg(p_scene_sm_response_pong.newBuilder());
				
			}

		};

		final TcpClient client = new TcpClient();

		client.registerSendEventIdByProto(1, p_sm_scene_request_ping.class);
		client.registerSocketEvent(2, pongProcessor);

		client.start();

		final Channel channel = client.connect("localhost", 8080);
		channel.writeAndFlush(p_sm_scene_request_ping.newBuilder());


		// client.close();
	}
}

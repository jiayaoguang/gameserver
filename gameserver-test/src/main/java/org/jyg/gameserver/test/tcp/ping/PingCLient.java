package org.jyg.gameserver.test.tcp.ping;

import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.TcpClient;
import io.netty.channel.Channel;
import org.jyg.gameserver.test.proto.p_test;
import org.jyg.gameserver.test.proto.p_test.p_scene_sm_response_pong;

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

		client.registerSendEventIdByProto(101, p_test.p_sm_scene_request_ping.class);
		client.registerSocketEvent(102, pongProcessor);

		client.start();

		client.connect("localhost", 8080);
		client.write(p_test.p_sm_scene_request_ping.newBuilder());


		// client.close();
	}
}

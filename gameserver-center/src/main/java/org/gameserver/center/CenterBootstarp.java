package org.gameserver.center;

import org.jyg.gameserver.core.enums.ProtoEnum;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import com.jyg.proto.p_auth_sm.p_auth_sm_request_send_token;
import com.jyg.proto.p_auth_sm.p_sm_auth_response_receive_token;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstarp;
import org.jyg.gameserver.core.util.TokenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class CenterBootstarp {
	public static void main(String[] args) throws Exception {

		final int wsport = 8088;

		GameServerBootstarp bootstarp = new GameServerBootstarp();

		Map<String, Object> tokenMap = new HashMap<>();

		ProtoProcessor<p_auth_sm_request_send_token> getTokenProcessor = new ProtoProcessor<p_auth_sm_request_send_token>(
				p_auth_sm_request_send_token.getDefaultInstance()) {
			@Override
			public void process(Session session, p_auth_sm_request_send_token msg) {
				System.out.println("i just get token , id" + msg.getRequestId());
				p_sm_auth_response_receive_token.Builder builder = p_sm_auth_response_receive_token.newBuilder();
				String token = TokenUtil.getToken();

				tokenMap.put(token, 1);
				builder.setToken(token);
				builder.setIp("127.0.0.1");
				builder.setPort(wsport);
				builder.setRequestId(msg.getRequestId());
				session.writeMessage(builder);
			}

		};

		System.out.println(getTokenProcessor.getProtoClassName());

		bootstarp.registerSocketEvent(ProtoEnum.P_AUTH_SM_REQUEST_SEND_TOKEN.getEventId(), getTokenProcessor);

		bootstarp.registerSendEventIdByProto(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(),
				p_sm_auth_response_receive_token.class);

		//

		bootstarp.addTcpService(9001);
		bootstarp.addWebSocketService(8088);

		bootstarp.start();
	}
}

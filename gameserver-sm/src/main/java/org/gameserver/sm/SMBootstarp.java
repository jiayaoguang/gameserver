package org.gameserver.sm;

import java.util.HashMap;
import java.util.Map;

import com.jyg.enums.ProtoEnum;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.net.TcpService;
import com.jyg.net.SocketService;
import com.jyg.net.WebSocketService;
import com.jyg.proto.p_auth_sm.p_auth_sm_request_send_token;
import com.jyg.proto.p_auth_sm.p_sm_auth_response_receive_token;
import com.jyg.startup.GameServerBootstarp;
import com.jyg.util.TokenUtil;

/**
 * Hello world!
 *
 */
public class SMBootstarp {
	public static void main(String[] args) throws Exception {

		final int wsport = 8088;

		GameServerBootstarp bootstarp = new GameServerBootstarp();

		Map<String, Object> tokenMap = new HashMap<>();

		ProtoProcessor<p_auth_sm_request_send_token> getTokenProcessor = new ProtoProcessor<p_auth_sm_request_send_token>(
				p_auth_sm_request_send_token.getDefaultInstance()) {
			@Override
			public void processProtoMessage(p_auth_sm_request_send_token msg, ProtoResponse response) {
				System.out.println("i just get token , id" + msg.getRequestId());
				p_sm_auth_response_receive_token.Builder builder = p_sm_auth_response_receive_token.newBuilder();
				String token = TokenUtil.getToken();

				tokenMap.put(token, 1);
				builder.setToken(token);
				builder.setIp("127.0.0.1");
				builder.setPort(wsport);
				builder.setRequestId(msg.getRequestId());
				response.writeMsg(builder);
			}

		};

		System.out.println(getTokenProcessor.getProtoClassName());

		bootstarp.registerSocketEvent(ProtoEnum.P_AUTH_SM_REQUEST_SEND_TOKEN.getEventId(), getTokenProcessor);

		bootstarp.registerSendEventIdByProto(ProtoEnum.P_SM_AUTH_RESPONSE_RECEIVE_TOKEN.getEventId(),
				p_sm_auth_response_receive_token.class);

		//
		TcpService socketService = new SocketService(9001);

		TcpService wssocketService = new WebSocketService(wsport);

		bootstarp.addService(socketService);

		bootstarp.addService(wssocketService);

		bootstarp.start();
	}
}

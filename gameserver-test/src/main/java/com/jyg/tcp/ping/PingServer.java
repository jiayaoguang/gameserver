package com.jyg.tcp.ping;

import org.jyg.gameserver.core.processor.ProtoProcessor;
import com.jyg.proto.p_sm_scene.p_scene_sm_response_pong;
import com.jyg.proto.p_sm_scene.p_sm_scene_request_ping;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstarp;


/**
 * Hello world!
 */
public class PingServer {
    public static void main(String[] args) throws Exception {
        GameServerBootstarp bootstarp = new GameServerBootstarp();
        bootstarp.registerSocketEvent(1, new PingProcessor());
        bootstarp.registerSendEventIdByProto(2, p_scene_sm_response_pong.class);
        //开端口服务
        bootstarp.addTcpService(8080);
        bootstarp.start();
    }

    public static class PingProcessor extends ProtoProcessor<p_sm_scene_request_ping> {

        public PingProcessor() {
            super(p_sm_scene_request_ping.getDefaultInstance());
        }

        @Override
        public void process(Session session, p_sm_scene_request_ping msg) {
            System.out.println("ok , i see ping");
            session.writeMessage(p_scene_sm_response_pong.newBuilder());
        }
    }
}

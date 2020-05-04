package com.jyg.tcp.asyn;

import com.jyg.processor.ProtoProcessor;
import com.jyg.proto.p_sm_scene.p_scene_sm_chat;
import com.jyg.proto.p_sm_scene.p_sm_scene_chat;
import com.jyg.proto.p_test.p_scene_sm_response_pong;
import com.jyg.proto.p_test.p_sm_scene_request_ping;
import com.jyg.session.Session;
import com.jyg.startup.TcpClient;
import org.junit.Test;

/**
 * Hello world!
 *
 */
public class AsynClientTest01
{
    public static void main( String[] args ) throws Exception
    {
    	

        TcpClient client = new TcpClient();
        

        client.registerSendEventIdByProto(1, p_sm_scene_request_ping.class);
        
        client.start();

        client.connect("localhost",8080);

        client.write(p_sm_scene_request_ping.newBuilder());
        
//        client.close();
    }
    
}

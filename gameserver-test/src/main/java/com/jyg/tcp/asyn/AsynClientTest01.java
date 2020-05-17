package com.jyg.tcp.asyn;

import com.jyg.proto.p_test.p_sm_scene_request_ping;
import org.jyg.gameserver.core.startup.TcpClient;

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

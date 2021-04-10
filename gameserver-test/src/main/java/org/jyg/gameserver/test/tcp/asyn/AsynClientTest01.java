package org.jyg.gameserver.test.tcp.asyn;

import org.jyg.gameserver.test.proto.p_test.p_sm_scene_request_ping;
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
        

        client.registerSendEventIdByProto(101, p_sm_scene_request_ping.class);
        
        client.start();

        client.connect("localhost",8080);

        client.write(p_sm_scene_request_ping.newBuilder().build());
        
//        client.close();
    }
    
}

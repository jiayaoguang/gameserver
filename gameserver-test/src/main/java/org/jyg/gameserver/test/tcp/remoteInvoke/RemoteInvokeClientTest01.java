package org.jyg.gameserver.test.tcp.remoteInvoke;

import org.jyg.gameserver.core.anno.InvokeName;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class RemoteInvokeClientTest01 {
    public static void main(String[] args) throws Exception {

        TcpClient client = new TcpClient("localhost", 8088);

        client.start();


        IRemoteInvoke remoteInvoke = client.getDefaultConsumer().createRemoteInvoke( Test03Invoke.class ,  client);
        remoteInvoke.invoke(null,new HashMap<>());


        Thread.sleep(10000);

        client.close();
        client.stop();
    }


    @InvokeName(name = "hello")
    public static class In implements IRemoteInvoke{

        @Override
        public void invoke(GameConsumer gameConsumer, Map<String,Object> paramMap) {
            AllUtil.println("hello world");
        }
    }




}

package org.jyg.gameserver.test.tcp.remoteInvoke;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.anno.InvokeName;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.invoke.IRemoteInvoke;

/**
 * Hello world!
 */
public class RemoteInvokeClientTest01 {
    public static void main(String[] args) throws Exception {

        TcpClient client = new TcpClient();

        client.start();

        client.connect("localhost", 8088);

        IRemoteInvoke remoteInvoke = client.getDefaultConsumer().createRemoteInvoke( In.class ,  client.getSession());
        remoteInvoke.invoke(new JSONObject());


        Thread.sleep(10000);

        client.close();
        client.stop();
    }


    @InvokeName(name = "hello")
    public static class In implements IRemoteInvoke{

        @Override
        public void invoke(JSONObject paramJson) {
            AllUtil.println("hello world");
        }
    }




}

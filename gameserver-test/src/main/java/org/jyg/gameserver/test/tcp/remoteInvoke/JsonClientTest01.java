package org.jyg.gameserver.test.tcp.remoteInvoke;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.IRemoteInvoke;
import org.jyg.gameserver.test.tcp.jsonMsg.JsonServerTest01;

/**
 * Hello world!
 */
public class JsonClientTest01 {
    public static void main(String[] args) throws Exception {

        TcpClient client = new TcpClient();

        client.start();
        client.connect("localhost", 8088);

        client.connect("localhost", 8088);

        IRemoteInvoke remoteInvoke = client.getDefaultConsumer().createRemoteInvoke( In.class ,  client.getSession());
        remoteInvoke.invoke(new JSONObject());


        Thread.sleep(10000);

        client.close();
        client.stop();
    }


    public static class In implements IRemoteInvoke{

        @Override
        public void invoke(JSONObject paramJson) {
            AllUtil.println("hello world");
        }
    }




}

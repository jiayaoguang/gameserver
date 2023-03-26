package org.jyg.gameserver.test;

import cn.hutool.core.codec.Base64Decoder;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.msg.ProtostuffMsgCodec;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.db.util.CreateTableUtil;
import org.jyg.gameserver.test.db.Maik;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class JygTest02 {


    @Test
    public void testDecode(){
        String s = "ssss";

        byte[] bs = Base64Decoder.decode(s);

        for(int i = 0;i<bs.length;i++){
            bs[i]--;
        }


        AllUtil.println(new String(bs));


    }

    @Test
    public void testSpring(){
        ApplicationEvent applicationEvent = null;

        ApplicationContext context = null;
        context.publishEvent(null);
        ApplicationListener applicationListener;
    }



    @Test
    public void testJson() throws Exception {

       ProtostuffMsgCodec protostuffMsgCodec = new ProtostuffMsgCodec(ConsumerEventData.class , 10000);
//
        ConsumerEventData consumerEventDataMsg = new ConsumerEventData();

        Pa maik = new Pa();
        maik.setName("2222");

        consumerEventDataMsg.setData(maik);
        AllUtil.println(JSONObject.toJSONString(consumerEventDataMsg));

        byte[] bs1 = protostuffMsgCodec.encode(consumerEventDataMsg);

        AllUtil.println(bs1.length);



        byte[] bs2 = protostuffMsgCodec.encode(consumerEventDataMsg);
        AllUtil.println(bs2.length);

//        byte[] newbs = {10,44,-6,7,31,111,114,103,46,106,121,103,46,103,97,109,101,115,101,114,118,101,114,46,116,101,115,116,46,100,98,46,77,97,105,107,8,0,16,0,26,4,50,50,50,50};
//
//        byte[] pabs = {10,45,-6,7,36,111,114,103,46,106,121,103,46,103,97,109,101,115,101,114,118,101,114,46,116,101,115,116,46,74,121,103,84,101,115,116,48,50,36,80,97,10,4,50,50,50,50};
//
//
//        ConsumerEventData consumerEventDataMsg1 = (ConsumerEventData)protostuffMsgCodec.decode( pabs );
//
//
//
//        AllUtil.println(((Maik)consumerEventDataMsg1.getData()).getContent());

//        AllUtil.println(protostuffMsgCodec.encode(consumerEventDataMsg).length);
//        AllUtil.println(JSONObject.toJSONString(consumerEventDataMsg).getBytes().length);



    }

    public static class Pa{
        String name = "";

        String ip = "";


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }





    public static class ConsumerEventData implements ByteMsgObj{


        public Object data;



        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }


    @Test
    public void testCreateSql(){
        String sql = CreateTableUtil.getCreateTableSql(Maik.class);
        AllUtil.println(sql);
    }


}

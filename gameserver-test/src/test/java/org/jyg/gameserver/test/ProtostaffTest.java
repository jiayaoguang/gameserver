package org.jyg.gameserver.test;

import org.junit.Test;
import org.jyg.gameserver.core.util.AllUtil;

/**
 * create by jiayaoguang on 2021/7/3
 */
public class ProtostaffTest {

    public static class Info{
        private long id;

        private String name;


        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    @Test
    public void test(){

        System.setProperty("protostuff.runtime.use_sun_misc_unsafe" , "true" );

        Info info = new Info();
        info.setId(111);
        info.setName("232314");


        byte[] bytes = ProtostaffUtils.serializeToByte(info);
        Info info2 = ProtostaffUtils.deserializeFromByte(bytes , Info.class);

        AllUtil.println(info2.id  + " : " + info2.name);

    }

}

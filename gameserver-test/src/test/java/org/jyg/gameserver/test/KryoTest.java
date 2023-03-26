//package org.jyg.gameserver.test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.annotation.JSONField;
//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Input;
//import com.esotericsoftware.kryo.io.Output;
//import com.esotericsoftware.kryo.serializers.JavaSerializer;
//import org.junit.Test;
//import org.jyg.gameserver.core.util.ExecTimeUtil;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.Serializable;
//
///**
// * create by jiayaoguang on 2021/6/27
// */
//
///**
// * Kryo序列化与反序列化操作
// * <dependency>
// * <groupId>com.esotericsoftware</groupId>
// * <artifactId>kryo-shaded</artifactId>
// * <version>4.0.0</version>
// * </dependency>
// *
// * @author Hxm
// */
//@SuppressWarnings({"all"})
//public class KryoTest {
//
//    static Kryo kryo = new Kryo();
//    static {
//        kryo.setReferences(false);
//    }
//
//    /**
//     * 序列化
//     *
//     * @param obj 待序列化对象
//     * @return base64字符串
//     */
//    public static  <T extends Serializable> byte[] serialize(T obj) {
//
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
//        Output output = new Output(baos);
//        kryo.writeObject(output, obj);
//        output.flush();
//        output.close();
//
//        byte[] b = baos.toByteArray();
//        try {
//            baos.flush();
//            baos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return b;
//    }
//
//    /**
//     * 反序列化
//     *
//     * @param objStr 进行反序列化的base64字符串
//     * @param clazz  反序列化目标类型
//     * @return 对象
//     */
//    public static <T extends Serializable> T deserialize(byte[] bytes, Class<T> clazz) {
//
//        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//        Input input = new Input(bais);
//        return (T) kryo.readObject(input , clazz);
//    }
//
//
//    public static class Simple implements Serializable{
//        private int id;
//        @JSONField(name = "1")
//        private String name;
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }
//
//
//    @Test
//    public void testKryo01(){
//
//        System.setProperty("protostuff.runtime.use_sun_misc_unsafe" , "true" );
//
//        Simple simple = new Simple();
//        simple.id = 10000;
//        simple.name = "hello";
//        kryo.setReferences(true);
//        ExecTimeUtil.exec("json serialize",()->{
//            for(int i =0;i<1000000;i++){
//                JSONObject.toJSONString(simple);
//            }
//        });
//
//        ExecTimeUtil.exec("kryo serialize",()->{
//
//            kryo.register(Simple.class, new JavaSerializer());
//            for(int i =0;i<1000000;i++){
//                byte[] bytes = serialize(simple);
//            }
//        });
//
//        ExecTimeUtil.exec("json deserialize",()->{
//            String str = JSONObject.toJSONString(simple);
//            for(int i =0;i<1000000;i++){
//                JSONObject.parseObject(str , Simple.class);
//            }
//        });
//
//        ExecTimeUtil.exec("kryo deserialize",()->{
//
//            kryo.register(Simple.class, new JavaSerializer());
//            byte[] bytes = serialize(simple);
//            for(int i =0;i<1000000;i++){
//                deserialize(bytes , Simple.class);
//            }
//        });
//
//
//
//        ExecTimeUtil.exec("staff serialize",()->{
//
//            kryo.register(Simple.class, new JavaSerializer());
//            for(int i =0;i<1000000;i++){
//                ProtostaffUtils.serializeToByte(simple);
//            }
//        });
//
//        ExecTimeUtil.exec("staff deserialize",()->{
//            byte[] bytes = ProtostaffUtils.serializeToByte(simple);
//            for(int i =0;i<1000000;i++){
//                ProtostaffUtils.deserializeFromByte(bytes , Simple.class);
//            }
//        });
//
//    }
//
//
//}
//
//

package org.jyg.gameserver.core.util;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by jiayaoguang on 2019/8/31.
 */
public class AllUtil {


    public static byte[] getIP() {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            byte[] internalIP = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        byte[] ipByte = ip.getAddress();
                        if (ipByte.length == 4) {
                            if (ipCheck(ipByte)) {
                                if (!isInternalIP(ipByte)) {
                                    return ipByte;
                                } else if (internalIP == null) {
                                    internalIP = ipByte;
                                }
                            }
                        }
                    }
                }
            }
            if (internalIP != null) {
                return internalIP;
            } else {
                throw new RuntimeException("Can not get local ip");
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not get local ip", e);
        }
    }


    private static boolean ipCheck(byte[] ip) {
        if (ip.length != 4) {
            throw new RuntimeException("illegal ipv4 bytes");
        }

//        if (ip[0] == (byte)30 && ip[1] == (byte)10 && ip[2] == (byte)163 && ip[3] == (byte)120) {
//        }

        if (ip[0] >= (byte) 1 && ip[0] <= (byte) 126) {
            if (ip[1] == (byte) 1 && ip[2] == (byte) 1 && ip[3] == (byte) 1) {
                return false;
            }
            if (ip[1] == (byte) 0 && ip[2] == (byte) 0 && ip[3] == (byte) 0) {
                return false;
            }
            return true;
        } else if (ip[0] >= (byte) 128 && ip[0] <= (byte) 191) {
            if (ip[2] == (byte) 1 && ip[3] == (byte) 1) {
                return false;
            }
            if (ip[2] == (byte) 0 && ip[3] == (byte) 0) {
                return false;
            }
            return true;
        } else if (ip[0] >= (byte) 192 && ip[0] <= (byte) 223) {
            if (ip[3] == (byte) 1) {
                return false;
            }
            if (ip[3] == (byte) 0) {
                return false;
            }
            return true;
        }
        return false;
    }


    public static boolean isInternalIP(byte[] ip) {
        if (ip.length != 4) {
            throw new RuntimeException("illegal ipv4 bytes");
        }

        //10.0.0.0~10.255.255.255
        //172.16.0.0~172.31.255.255
        //192.168.0.0~192.168.255.255
        if (ip[0] == (byte) 10) {

            return true;
        } else if (ip[0] == (byte) 172) {
            if (ip[1] >= (byte) 16 && ip[1] <= (byte) 31) {
                return true;
            }
        } else if (ip[0] == (byte) 192) {
            if (ip[1] == (byte) 168) {
                return true;
            }
        }
        return false;
    }

    public void test() {
        byte[] ipbytes = getIP();

        printIpV4(ipbytes);
    }


    public void printIpV4(byte[] ipBytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ipBytes.length; i++) {
            byte b = ipBytes[i];
            sb.append((b < 0 ? (b + 256) : b));
            if(i != ipBytes.length-1){
                sb.append(".");
            }
        }
        println(" ip :" + sb.toString());
    }


    public static void println(Object obj){
        System.out.println(obj);
    }

    public static void println(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b:bytes){
            sb.append(b).append(',');
        }
        System.out.println(sb.toString());
    }

    public static void properties2Object(final String fileName, final Object object) {

        File file = new File(fileName);

        if(!file.exists()){
            MyLoggerFactory.DEFAULT_LOGGER.error(" config {} file not exist " , fileName);
            return;
        }

        if(file.isDirectory()){
            MyLoggerFactory.DEFAULT_LOGGER.error(" config {} file isDirectory " , fileName);
            return;
        }


        try {
//            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            Properties properties = new Properties();
            try (InputStream in = new BufferedInputStream(new FileInputStream(file));){
                properties.load(in);
            }
            properties2Object(properties, object);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void properties2Object(final Properties p, final Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String mn = method.getName();
            if (!mn.startsWith("set")) {
                continue;
            }
            try {
                String tmp = mn.substring(4);
                String first = mn.substring(3, 4);

                String key = first.toLowerCase() + tmp;
                String property = p.getProperty(key);
                if (property == null) {
//                    println("property == null");
                    continue;
                }
                Class<?>[] pt = method.getParameterTypes();
                if (pt == null || pt.length == 0) {
                    continue;
                }
                String cn = pt[0].getSimpleName();
                Object arg = null;
                switch (cn) {
                    case "int":
                    case "Integer":
                        arg = Integer.parseInt(property);
                        break;
                    case "long":
                    case "Long":
                        arg = Long.parseLong(property);
                        break;
                    case "double":
                    case "Double":
                        arg = Double.parseDouble(property);
                        break;
                    case "boolean":
                    case "Boolean":
                        arg = Boolean.parseBoolean(property);
                        break;
                    case "float":
                    case "Float":
                        arg = Float.parseFloat(property);
                        break;
                    case "String":
                        arg = property;
                        break;
                    default:
                        continue;
                }
                MyLoggerFactory.DEFAULT_LOGGER.info("set field : {} value : {} " , key , arg);
                method.invoke(object, arg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param context
     * @param msg
     * @param buf
     */
    public static void writeToBuf(Context context, MessageLite msg, ByteBuf buf) {
        Class<? extends MessageLite> protoClazz = msg.getClass();
        MyLoggerFactory.DEFAULT_LOGGER.info("deal threadName : " + Thread.currentThread().getName());
        byte[] msgBytes = msg.toByteArray();
//    if (msg instanceof GeneratedMessageV3) {
//       bytes = msg.toByteArray();
//       protoClazz = ((GeneratedMessageV3)msg).getClass();
//    } else if (msg instanceof GeneratedMessageV3.Builder) {
////         GeneratedMessageV3 messageLite =  ((GeneratedMessageV3.Builder) msg).build();
////         bytes = messageLite.toByteArray();
////         protoClazz = messageLite.getClass();
//       throw new IllegalArgumentException("Unknow message type");
//    }else {
//       throw new IllegalArgumentException("Unknow message type");
//    }

        if (msgBytes == null) {
            throw new IllegalArgumentException("not MessageLiteOrBuilder");
        }
        int eventId = context.getMsgIdByProtoClass(protoClazz);
        if (eventId <= 0) {
            MyLoggerFactory.DEFAULT_LOGGER.error("unknow eventid");
            return;
        }

//    if(context.getServerConfig().isUseGzip()){
//       bytes = ZipUtil.gzip(bytes);
//    }

        writeToBuf(eventId , buf , msgBytes);
    }


    /**
     *
     * @param context
     * @param byteMsgObj
     * @param buf
     */
    public static void writeToBuf(Context context, ByteMsgObj byteMsgObj, ByteBuf buf) {
        Class<? extends ByteMsgObj> byteMsgObjClazz = byteMsgObj.getClass();
        MyLoggerFactory.DEFAULT_LOGGER.info("deal threadName : " + Thread.currentThread().getName());


        int eventId = context.getMsgIdByByteMsgObj(byteMsgObjClazz);
        if (eventId <= 0) {
            MyLoggerFactory.DEFAULT_LOGGER.error("unknow eventid");
            return;
        }


        AbstractMsgCodec msgCodec = context.getMsgCodec(eventId);

        byte[] msgBytes = msgCodec.encode(byteMsgObj);

        writeToBuf(eventId , buf , msgBytes);

    }

    private static void writeToBuf(int msgId ,ByteBuf buf, byte[] msgBytes){
        int protoLen = 4 + msgBytes.length;
//    ByteBuf buf = ctx.alloc().directBuffer(protoLen);
        buf.writeInt(protoLen);
        buf.writeInt(msgId);
        buf.writeBytes(msgBytes);
    }


}

package org.jyg.gameserver.core.util;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.msg.DefaultMsg;

import java.io.*;
import java.lang.reflect.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

/**
 * Created by jiayaoguang on 2019/8/31.
 */
public class AllUtil {

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];


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

    public static void println(List<?> list){
        StringBuilder sb = new StringBuilder();
        for(Object o:list){
            sb.append(o).append(',');
        }
        System.out.println(sb.toString());
    }


    /**
     *
     * @param gameContext context
     * @param msg msg
     * @param buf buf
     */
    public static void writeToBuf(GameContext gameContext, MessageLite msg, ByteBuf buf) {
        Class<? extends MessageLite> protoClazz = msg.getClass();
//        Logs.DEFAULT_LOGGER.info("deal threadName : " + Thread.currentThread().getName());
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
        int eventId = gameContext.getMsgIdByProtoClass(protoClazz);
        if (eventId <= 0) {
            Logs.DEFAULT_LOGGER.error("unknow eventid  protoClazz : {}" , protoClazz);
            return;
        }

//    if(context.getServerConfig().isUseGzip()){
//       bytes = ZipUtil.gzip(bytes);
//    }

        writeToBuf(eventId , buf , msgBytes);
    }


    /**
     *
     * @param gameContext context
     * @param byteMsgObj byteMsgObj
     * @param buf buf
     */
    public static void writeToBuf(GameContext gameContext, ByteMsgObj byteMsgObj, ByteBuf buf) {
        Class<? extends ByteMsgObj> byteMsgObjClazz = byteMsgObj.getClass();
//        Logs.DEFAULT_LOGGER.info("deal threadName : " + Thread.currentThread().getName());

        if(byteMsgObj instanceof DefaultMsg){
            DefaultMsg defaultMsg = (DefaultMsg)byteMsgObj;
            writeToBuf(defaultMsg.getMsgId() , buf , defaultMsg.getMsgData());
            return;

        }


        int eventId = gameContext.getMsgIdByByteMsgObj(byteMsgObjClazz);
        if (eventId <= 0) {
            Logs.DEFAULT_LOGGER.error("unknow eventid ,class {}" + byteMsgObjClazz.getSimpleName());
            throw new IllegalArgumentException("unknow eventid "+ byteMsgObjClazz.getSimpleName());
        }


        AbstractMsgCodec msgCodec = gameContext.getMsgCodec(eventId);

        if(msgCodec == null){
            Logs.DEFAULT_LOGGER.error("unknow msgCodec eventId : {}",eventId);
            throw new IllegalArgumentException("unknow msgCodec eventId : "+eventId);
        }

        byte[] msgBytes = EMPTY_BYTE_ARRAY;
        try {
            msgBytes = msgCodec.encode(byteMsgObj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        writeToBuf(eventId , buf , msgBytes);


    }

    private static void writeToBuf(int msgId ,ByteBuf buf, byte[] msgBytes){
        int protoLen = 4 + msgBytes.length;
//    ByteBuf buf = ctx.alloc().directBuffer(protoLen);
        buf.writeInt(protoLen);
        buf.writeInt(msgId);

        if(msgBytes.length > 0){
            buf.writeBytes(msgBytes);
        }
    }




    public static String getChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }



    public static boolean isStatic(Field field){
        return Modifier.isStatic(field.getModifiers());
    }


    public static List<Field> getClassObjectFields(Class<?> clazz){

        List<Field> classObjectFields = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if(isStatic(field)){
                continue;
            }
            classObjectFields.add(field);
        }


        Class<?> superClass = clazz.getSuperclass();

        for(;superClass != null;){
            Field[] superCalssFields = superClass.getDeclaredFields();
            for(Field field : superCalssFields){
                if(isStatic(field)){
                    continue;
                }
                classObjectFields.add(field);
            }
            superClass = superClass.getSuperclass();
        }


        return classObjectFields;
    }

    /**
     * 获取文件夹里指定后缀名的所有文件
     * @param rootFile 根文件夹目录
     * @param needFileSuffix 指定文件名后缀
     * @return 文件列表
     */
    public static List<File> getChildFiles(File rootFile , String needFileSuffix) {
        List<File> childFiles = new ArrayList<>();
        if (!rootFile.exists()) {
            return childFiles;
        }
        if (!rootFile.isDirectory()) {
            Logs.DEFAULT_LOGGER.error("rootFile {} not dir" , rootFile.getAbsolutePath());
            return childFiles;
        }

        Queue<File> dirQueue = new LinkedList<>();
        dirQueue.add(rootFile);

        for (; dirQueue.size() > 0; ) {

            File curDir = dirQueue.poll();

            File[] files = curDir.listFiles();
            if (files == null) {
                continue;
            }
            for (File child : files) {
                if (child.isDirectory()) {
                    dirQueue.add(child);
                } else {
                    if (child.getName().endsWith(needFileSuffix)) {
                        childFiles.add(child);
                    }
                }
            }
        }

        return childFiles;
    }


    public static Type getClassGenericType(Class<Object> clazz){
        Type superClass = clazz.getGenericSuperclass();
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        Type _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];

        return _type;
    }


    public static String getChannelRemoteIp(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if(addr.length() <= 0){
            return "";
        }

        int index = addr.lastIndexOf("/");
        int colonIndex = addr.lastIndexOf(":");

        if (index >= 0) {
            if (colonIndex > index) {
                return addr.substring(index + 1, colonIndex);
            }else {
                return addr.substring(index + 1);
            }
        }

        return addr;

    }


}

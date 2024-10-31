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
            Logs.DEFAULT_LOGGER.error("make exception : " ,e);
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





}

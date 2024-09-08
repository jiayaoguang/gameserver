package org.jyg.gameserver.core.handle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.event.UdpMsgEvent;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import io.netty.channel.socket.DatagramPacket;

/**
 * create by jiayaoguang on 2024/9/7
 */
public class UdpMsgDecoder extends SimpleChannelInboundHandler<DatagramPacket> {

    private final GameContext gameContext;

    private final boolean printRequestMsg;


    int random = RandomUtils.nextInt();

    public UdpMsgDecoder(GameContext gameContext) {

        this.gameContext = gameContext;
        this.printRequestMsg = gameContext.getServerConfig().isPrintRequestMsg();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        if(packet.sender() == null){
            Logs.DEFAULT_LOGGER.error(" udp sender is null ignore udp msg");
            return;
        }
        final String addrRemote = packet.sender().toString();


        String senderHost = packet.sender().getHostName();
        int senderPort = packet.sender().getPort();

        if(StringUtils.isEmpty(senderHost) ){
            Logs.DEFAULT_LOGGER.error(" udp senderHost is null ignore udp msg");
            return;
        }
        if(senderPort <= 0 ){
            Logs.DEFAULT_LOGGER.error(" udp senderHost {} senderPort {} <= 0 ignore udp msg",senderHost, senderPort);
            return;
        }



        ByteBuf frame = packet.content();
        try {

            //TODO 暂时忽略长度，之后处理
            int len = frame.readInt();

            int msgId = frame.readInt();
//            Logs.DEFAULT_LOGGER.debug("cnf:" + frame.refCnt());
            AbstractMsgCodec<?> msgCodec = gameContext.getMsgCodec(msgId);
            if (msgCodec == null) {

                int readableBytes = frame.readableBytes();
                final byte[] dstBytes = new byte[frame.readableBytes()];
                if (readableBytes > 0) {
                    frame.getBytes(frame.readerIndex(), dstBytes);
                }

//                UnknownMsgEvent unknownMsgEvent = new UnknownMsgEvent(msgId, dstBytes, ctx.channel());
//                gameContext.getConsumerManager().publishEvent(gameContext.getMainConsumerId(), unknownMsgEvent);
                Logs.DEFAULT_LOGGER.error(" {} send unknown udp msg {}",addrRemote , msgId);
                return;
            }

            int readableBytes = frame.readableBytes();


            final byte[] dstBytes = new byte[readableBytes];
            if (readableBytes > 0) {
                frame.getBytes(frame.readerIndex(), dstBytes);
            } else {
//                LOGGER.info(" readableBytes <= 0 ,id : {} ", msgId);
            }

            Object msgObj;

            try {
                msgObj = msgCodec.decode(dstBytes);
            } catch (Exception e) {
                Logs.DEFAULT_LOGGER.error(" msg decode make exception, msgCodec type : {}  , exception {}", msgCodec.getClass().getSimpleName(), e.getCause());
                return;
            }

            if (printRequestMsg) {
                Logs.DEFAULT_LOGGER.info("addrRemote {} request msgId {} msg {}", addrRemote, msgId, msgObj.getClass().getSimpleName());
            }

            UdpMsgEvent udpMsgEvent = new UdpMsgEvent(msgId, msgObj, ctx.channel(),senderHost,senderPort);

            gameContext.getConsumerManager().publishEvent(gameContext.getMainConsumerId(), udpMsgEvent);
        }catch (Exception e){
            Logs.DEFAULT_LOGGER.error("addrRemote {} msg decode make exception {}", addrRemote, ExceptionUtils.getStackTrace(e));
        }  finally {

        }


    }
}
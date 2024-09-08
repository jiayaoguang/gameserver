package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import org.jyg.gameserver.core.data.UdpMsgInfo;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.IpUtil;
import org.jyg.gameserver.core.util.Logs;

import java.net.InetSocketAddress;

/**
 * create by jiayaoguang on 2024/9/7
 */
@ChannelHandler.Sharable
public class UdpMsgEncoder extends MessageToByteEncoder<UdpMsgInfo> {

    protected final GameContext gameContext;

    private final boolean printResponseMsg;

    public UdpMsgEncoder(GameContext gameContext) {
        this.gameContext = gameContext;
        this.printResponseMsg = gameContext.getServerConfig().isPrintResponseMsg();
    }



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {


        ByteBuf buf = null;
        try {

            if (acceptOutboundMessage(msg)) {
                @SuppressWarnings("unchecked")
                UdpMsgInfo cast = (UdpMsgInfo) msg;
                buf = allocateBuffer(ctx, cast, isPreferDirect());
                try {
                    encode(ctx, cast, buf);
                } finally {
                    ReferenceCountUtil.release(cast);
                }

                if (buf.isReadable()) {
                    DatagramPacket packet = new DatagramPacket(buf, new InetSocketAddress(cast.getHost(), cast.getPort()));
                    ctx.write(packet, promise);
                } else {
                    buf.release();
                    ctx.write(Unpooled.EMPTY_BUFFER, promise);
                }
                buf = null;
            } else {
                ctx.write(msg, promise);
            }

        } catch (EncoderException e) {
            throw e;
        } catch (Throwable e) {
            throw new EncoderException(e);
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, UdpMsgInfo udpMsgInfo, ByteBuf buf) {
        Object msg= udpMsgInfo.getMsg();
        if (msg instanceof MessageLite) {
            MessageLite messageLite = (MessageLite) msg;
            AllUtil.writeToBuf(gameContext, messageLite, buf);
        } else if (msg instanceof ByteMsgObj) {
            ByteMsgObj byteMsgObj = (ByteMsgObj) msg;
            AllUtil.writeToBuf(gameContext, byteMsgObj, buf);
        } else {
            Logs.DEFAULT_LOGGER.error("encode msg fail , unknown msg type : {}", msg.getClass().getCanonicalName());
        }
//		out.add(buf);

        if (printResponseMsg) {
            Logs.DEFAULT_LOGGER.info("response session {} msg {}", IpUtil.getChannelRemoteIp(ctx.channel()), msg.getClass().getSimpleName());
        }

    }

}


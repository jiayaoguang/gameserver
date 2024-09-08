package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang on 2022/2/4
 */
@ChannelHandler.Sharable
public class WebSocketMsgEncoder extends MessageToByteEncoder<Object> {

    protected final GameContext gameContext;

    public WebSocketMsgEncoder(GameContext gameContext) {
        this.gameContext = gameContext;
    }


    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        WebSocketFrame webSocketFrame = null;

        try {


            if (msg instanceof ByteMsgObj) {
                ByteMsgObj cast = (ByteMsgObj) msg;

                Class<? extends ByteMsgObj> byteMsgObjClazz = cast.getClass();
//        Logs.DEFAULT_LOGGER.info("deal threadName : " + Thread.currentThread().getName());

                int eventId = gameContext.getMsgIdByByteMsgObj(byteMsgObjClazz);
                if (eventId <= 0) {
                    Logs.DEFAULT_LOGGER.error("unknow eventid ,class {}" + byteMsgObjClazz.getSimpleName());
                    throw new IllegalArgumentException("unknow eventid "+ byteMsgObjClazz.getSimpleName());
                }


                AbstractMsgCodec msgCodec = gameContext.getMsgCodec(eventId);


                if(msgCodec == null){
                    Logs.DEFAULT_LOGGER.error("unknow msgCodec ,class {}" + byteMsgObjClazz.getSimpleName());
                    throw new IllegalArgumentException("unknow msgCodec "+ byteMsgObjClazz.getSimpleName());
                }

                byte[] msgBytes = msgCodec.encode(cast);

                webSocketFrame = new BinaryWebSocketFrame();
                webSocketFrame.content().writeInt(eventId);
                webSocketFrame.content().writeBytes(msgBytes);

                ctx.write(webSocketFrame, promise);

                webSocketFrame = null;
            } else if (msg instanceof MessageLite) {
                MessageLite cast = (MessageLite) msg;

                Class<? extends MessageLite> protoClass = cast.getClass();
//        Logs.DEFAULT_LOGGER.info("deal threadName : " + Thread.currentThread().getName());

                int eventId = gameContext.getMsgIdByProtoClass(protoClass);
                if (eventId <= 0) {
                    Logs.DEFAULT_LOGGER.error("unknow eventid ,class {}" , protoClass.getSimpleName());
                    throw new IllegalArgumentException("unknow eventid "+ protoClass.getSimpleName());
                }


                AbstractMsgCodec msgCodec = gameContext.getMsgCodec(eventId);


                if(msgCodec == null){
                    Logs.DEFAULT_LOGGER.error("unknow msgCodec ,class {}" , protoClass.getSimpleName());
                    throw new IllegalArgumentException("unknow msgCodec "+ protoClass.getSimpleName());
                }

                byte[] msgBytes = msgCodec.encode(cast);

                webSocketFrame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer((msgBytes)));

                ctx.write(webSocketFrame, promise);

                webSocketFrame = null;
            } else {
                ctx.write(msg, promise);
            }
        } catch (EncoderException var17) {
            throw var17;
        } catch (Throwable var18) {
            throw new EncoderException(var18);
        } finally {
            if (webSocketFrame != null) {
                webSocketFrame.release();
            }

        }

    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

    }

}

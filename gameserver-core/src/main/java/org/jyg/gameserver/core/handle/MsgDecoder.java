package org.jyg.gameserver.core.handle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.event.ChannelMsgEvent;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2018年3月13日 protobuf解码器
 */
public class MsgDecoder extends LengthFieldBasedFrameDecoder {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MsgDecoder.class);

    private final GameContext gameContext;

    public MsgDecoder(GameContext gameContext) {
        super(gameContext.getServerConfig().getMaxFrameLength(), 0, 4, 0, 4);
        this.gameContext = gameContext;
    }

    // @Override
    // public ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int
    // index, int length) {
    // ByteBuf buf = buffer.slice(index, length);
    // int eventId = buf.readInt();
    // ProtoProcessor<? extends GeneratedMessageV3> protProcessor =
    // dis.getSocketProcessor(eventId);
    //
    // if(protProcessor==null) {
    // return Unpooled.EMPTY_BUFFER;
    // }
    //
    // Parser<? extends MessageLite> parser = protProcessor.getProtoParser();
    //
    // try (ByteBufInputStream bis = new ByteBufInputStream(buf)) {
    // MessageLite messageLite = parser.parseFrom(bis);
    //
    // .publicEvent(EventType.RPC_MSG_COME, messageLite, ctx.channel() ,
    // eventId );
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // return Unpooled.EMPTY_BUFFER;
    // }


    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
//        ByteBuf frame = null;
        //TODO crc
//		try {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        try{

            int msgId = frame.readInt();
//            Logs.DEFAULT_LOGGER.debug("cnf:" + frame.refCnt());
            AbstractMsgCodec<?> msgCodec = gameContext.getMsgCodec(msgId);
            if (msgCodec == null) {

                int readableBytes = frame.readableBytes();
                final byte[] dstBytes = new byte[frame.readableBytes()];
                if(readableBytes > 0){
                    frame.getBytes(frame.readerIndex(), dstBytes);
                }
                gameContext.getConsumerManager().publicEventToDefault(EventType.REMOTE_UNKNOWN_MSG_COME, dstBytes, ctx.channel(), msgId );

                return null;
            }

            int readableBytes = frame.readableBytes();


            final byte[] dstBytes = new byte[readableBytes];
            if(readableBytes > 0){
                frame.getBytes(frame.readerIndex(), dstBytes);
            }else {
//                LOGGER.info(" readableBytes <= 0 ,id : {} ", msgId);
            }

            Object msgObj;

            try{
                msgObj = msgCodec.decode(dstBytes);
            }catch (Exception e){
                LOGGER.error(" msg decode make exception, msgCodec type : {}  , exception {}", msgCodec.getClass().getSimpleName(), e.getCause());
                throw e;
            }

            EventData eventData = new EventData();
            eventData.setChannel(ctx.channel());
            eventData.setData(msgObj);
            eventData.setEventId(msgId);

            ChannelMsgEvent channelMsgEvent = new ChannelMsgEvent(ctx.channel(),eventData);

            gameContext.getConsumerManager().publicEventToDefault(EventType.PUBLISH_EVENT, channelMsgEvent , msgId);


        }catch (Exception e){
            e.printStackTrace();
            final String addrRemote = AllUtil.getChannelRemoteAddr(ctx.channel());
            ctx.channel().close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Logs.DEFAULT_LOGGER.info("closeChannel: close the connection to remote address[{}] result: {}", addrRemote,
                            future.isSuccess());
                }
            });
        } finally {

//            Logs.DEFAULT_LOGGER.info("frame.refCnt() : " + frame.refCnt());

        }




//        byte[] dstBytes = new byte[frame.readableBytes()];
//        frame.getBytes(frame.readerIndex(), dstBytes);




//        try (ByteBufInputStream bis = new ByteBufInputStream(frame)) {
//            final MessageLite messageLite;
////			if (context.getServerConfig().isUseGzip()) {
////				messageLite = protoParser.parseFrom(ZipUtil.unGzip(bis , bis.available()));
////			}else {
//            messageLite = protoParser.parseFrom(bis);
////			}
//            context.getDefaultConsumer().publicEvent(EventType.RROTO_MSG_COME, messageLite, ctx.channel(), msgId);
//        }

//		}
//		catch (Exception e) {
//			ctx.channel().close().addListener(new ChannelFutureListener() {
//				@Override
//				public void operationComplete(ChannelFuture future) throws Exception {
//					// log
//				}
//			});
//			e.printStackTrace();
//		}
        // finally {
        // if(null!=frame)
        // frame.release();
        // }

        return null;
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }


}

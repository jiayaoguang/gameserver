package org.jyg.gameserver.core.handle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jyg.gameserver.core.event.NormalMsgEvent;
import org.jyg.gameserver.core.event.UnknownMsgEvent;
import org.jyg.gameserver.core.msg.AbstractMsgCodec;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.IpUtil;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by jiayaoguang at 2018年3月13日 protobuf解码器
 */
public class MsgDecoder extends LengthFieldBasedFrameDecoder {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MsgDecoder.class);

    private final GameContext gameContext;

    private final boolean printRequestMsg;

    public MsgDecoder(GameContext gameContext) {
        super(gameContext.getServerConfig().getMaxFrameLength(), 0, 4, 0, 4);
        this.gameContext = gameContext;
        this.printRequestMsg = gameContext.getServerConfig().isPrintRequestMsg();
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

                UnknownMsgEvent unknownMsgEvent = new UnknownMsgEvent( msgId , dstBytes , ctx.channel() );
                gameContext.getConsumerManager().publishEvent(gameContext.getMainConsumerId(), unknownMsgEvent);

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
                return null;
            }

            if(printRequestMsg){
                Logs.DEFAULT_LOGGER.info("session {} request msgId {} msg {}", IpUtil.getChannelRemoteIp(ctx.channel()), msgId, msgObj.getClass().getSimpleName());
            }

            NormalMsgEvent normalMsgEvent = new NormalMsgEvent(msgId , msgObj , ctx.channel());

            gameContext.getConsumerManager().publishEvent(gameContext.getMainConsumerId(),normalMsgEvent);


        }catch (Exception e){
            final String addrRemote = AllUtil.getChannelRemoteAddr(ctx.channel());
            LOGGER.error("addrRemote {} msg decode make exception {}", addrRemote, ExceptionUtils.getStackTrace(e));
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

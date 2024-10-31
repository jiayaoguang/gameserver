package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * create by jiayaoguang on 2020/9/5
 * proto 消息合并
 */
public class MsgMergeHandler extends MessageToByteEncoder<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger("ProtoMergeHandler");

    protected final GameContext gameContext;

// private final List<MessageLite> msgList = new ArrayList<>();

//    private long nextFlushTime = System.currentTimeMillis();

    private ByteBuf cacheBuf = null;

    private static final int MAX_BUF_LENGTH = 1024 * 1024;

    private int onceMergeMsgNum = 0;


    private CheckRunnable lastCheckRunnable;

    public class CheckRunnable implements Runnable{

        private boolean isFlush = false;
        Channel channel;


        public CheckRunnable(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {

            if(isFlush){
                return;
            }

            if(cacheBuf == null){
                return;
            }
            if(cacheBuf.readableBytes() <= 0){
                return;
            }

            try {
                Logs.DEFAULT_LOGGER.info(" ontime flush " + Thread.currentThread().getName() + " mergeMsgNum :" + onceMergeMsgNum + " , size : " + cacheBuf.readableBytes());
                channel.writeAndFlush(cacheBuf);

//                boolean isSuccess = channelFuture.isSuccess();
//                AllUtil.println(" send ============== isSuccess : " + isSuccess + "  " + channel.isActive());
            } catch (Exception e) {
                Logs.DEFAULT_LOGGER.error("make exception : " ,e);
            }finally {
                cacheBuf = null;
                onceMergeMsgNum = 0;
            }

        }
    }

    public MsgMergeHandler(GameContext gameContext) {
        this.gameContext = gameContext;
    }





    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {



//        ByteBuf buf = null;
        try {

            if(!(msg instanceof MessageLite) && !(msg instanceof ByteMsgObj)){
                ctx.writeAndFlush(msg, promise);
                return;
            }

            if (acceptOutboundMessage(msg)) {

//        if(this.nextFlushTime < now){
//            this.nextFlushTime = now + 10;
//        }


                if(cacheBuf == null){
                    cacheBuf = ctx.alloc().heapBuffer();
//                    this.nextFlushTime = now + 8;
                    CheckRunnable checkRunnable = new CheckRunnable(ctx.channel());
                    ctx.executor().schedule(checkRunnable, 4, TimeUnit.MILLISECONDS);
                    this.lastCheckRunnable = checkRunnable;
                }

//                LOGGER.info(" ========= " + cacheBuf.refCnt() + " == " + Thread.currentThread().getName());
//                if(msgList.isEmpty()){
//                    this.nextFlushTime = now + 10;
//                }
//                msgList.add(cast);
                if( msg instanceof MessageLite ){
                    MessageLite originProto = (MessageLite) msg;
                    AllUtil.writeToBuf(gameContext,originProto , cacheBuf);
                }else if( msg instanceof ByteMsgObj ){
                    ByteMsgObj byteMsgObj = (ByteMsgObj) msg;
                    AllUtil.writeToBuf(gameContext,byteMsgObj , cacheBuf);
                }else {
                    ctx.writeAndFlush(msg, promise);
                    return;
                }

                onceMergeMsgNum++;
                int byteSize = cacheBuf.readableBytes();
                if(byteSize < MAX_BUF_LENGTH ){
                    return;
                }
//                LOGGER.info(" ========= " + cacheBuf.refCnt() + " byteSize : " + byteSize);
                this.lastCheckRunnable.isFlush = true;

//                buf = allocateBuffer(ctx, cast, true);
//                try {
//                    encode(ctx, cast, buf);
//                } finally {
//                    ReferenceCountUtil.release(cast);
//                }
//                buf = cacheBuf;
                ctx.writeAndFlush(cacheBuf, promise);
                onceMergeMsgNum = 0;
                cacheBuf = null;
            } else {
                ctx.writeAndFlush(msg, promise);
            }
        } catch (EncoderException e) {
            throw e;
        } catch (Throwable e) {
            throw new EncoderException(e);
        } finally {
//            if (buf != null) {
//                buf.release();
//            }
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        if(cacheBuf != null){
            if(cacheBuf.refCnt() > 0){
                cacheBuf.release(cacheBuf.refCnt());
            }
        }

        if(lastCheckRunnable != null){
            lastCheckRunnable.isFlush = true;
        }
    }


}
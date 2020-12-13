package org.jyg.gameserver.core.handle;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * create by jiayaoguang on 2020/9/5
 * proto 消息合并
 */
public class ProtoMergeHandler  extends MessageToByteEncoder<MessageLite> {

    private static final Logger LOGGER = LoggerFactory.getLogger("ProtoMergeHandler");

    protected final Context context;

// private final List<MessageLite> msgList = new ArrayList<>();

//    private long nextFlushTime = System.currentTimeMillis();

    private ByteBuf cacheBuf = null;

    private static final int MAX_BUF_LENGTH = 1024;

    private int onceMergeMsgNum = 0;


    private CheckRunnable lastCheckRunnable;

    public class CheckRunnable implements Runnable{

        private boolean isFlush = false;
        ChannelHandlerContext channelHandlerContext;


        public CheckRunnable(ChannelHandlerContext channelHandlerContext) {
            this.channelHandlerContext = channelHandlerContext;
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
                channelHandlerContext.write(cacheBuf);
                Logs.DEFAULT_LOGGER.info(" ontime flush " + Thread.currentThread().getName() + " mergeMsgNum :" + onceMergeMsgNum);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                cacheBuf = null;
                onceMergeMsgNum = 0;
            }

        }
    }

    public ProtoMergeHandler(Context context) {
        this.context = context;
    }





    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {



//        ByteBuf buf = null;
        try {
            if (acceptOutboundMessage(msg)) {
                @SuppressWarnings("unchecked")
                MessageLite originProto = (MessageLite) msg;

//        if(this.nextFlushTime < now){
//            this.nextFlushTime = now + 10;
//        }


                if(cacheBuf == null){
                    cacheBuf = ctx.alloc().heapBuffer();
//                    this.nextFlushTime = now + 8;
                    CheckRunnable checkRunnable = new CheckRunnable(ctx);
                    ctx.executor().schedule(checkRunnable, 10, TimeUnit.MILLISECONDS);
                    this.lastCheckRunnable = checkRunnable;
                }

                LOGGER.info(" ========= " + cacheBuf.refCnt() + " == " + Thread.currentThread().getName());
//                if(msgList.isEmpty()){
//                    this.nextFlushTime = now + 10;
//                }
//                msgList.add(cast);

                AllUtil.writeToBuf(context,originProto , cacheBuf);
                onceMergeMsgNum++;
                int byteSize = cacheBuf.readableBytes();
                if(byteSize < MAX_BUF_LENGTH ){
                    return;
                }
                LOGGER.info(" ========= " + cacheBuf.refCnt() + " byteSize : " + byteSize);
                this.lastCheckRunnable.isFlush = true;

//                buf = allocateBuffer(ctx, cast, true);
//                try {
//                    encode(ctx, cast, buf);
//                } finally {
//                    ReferenceCountUtil.release(cast);
//                }
//                buf = cacheBuf;
                ctx.write(cacheBuf, promise);
                onceMergeMsgNum = 0;
                cacheBuf = null;
            } else {
                ctx.write(msg, promise);
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
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {
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
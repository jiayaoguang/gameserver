package org.jyg.gameserver.core.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.jyg.gameserver.core.constant.MsgIdConst;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.msg.PingByteMsg;
import org.jyg.gameserver.core.msg.ReadIdleMsgObj;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.Logs;

/**
 * create by jiayaoguang at 2021/5/29
 */
public class NettyConnectManageHandler extends ChannelDuplexHandler {

    private final Context context;


    private static final ReadIdleMsgObj READ_IDLE_OBJ = new ReadIdleMsgObj();

    public static final PingByteMsg PING_MSG =  new PingByteMsg();

    public NettyConnectManageHandler(Context context) {
        this.context = context;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
        Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "在线");

        context.getConsumerManager().publicEventToDefault(EventType.SOCKET_CONNECT_ACTIVE, null, ctx.channel(), 0);

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
        Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "掉线");

        context.getConsumerManager().publicEventToDefault(EventType.SOCKET_CONNECT_INACTIVE, null, ctx.channel(), 0);

        super.channelInactive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = AllUtil.getChannelRemoteAddr(ctx.channel());
                Logs.DEFAULT_LOGGER.warn("NETTY CLIENT PIPELINE: IDLE outtime [{}]", remoteAddress);
            } else if (event.state().equals(IdleState.READER_IDLE)) {
                context.getConsumerManager().publicEventToDefault(EventType.REMOTE_MSG_COME, READ_IDLE_OBJ, ctx.channel(), MsgIdConst.READ_OUTTIME);
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
//                context.getConsumerManager().publicEventToDefault(EventType.BYTE_OBJ_MSG_COME, READ_IDLE_OBJ, ctx.channel(), MsgIdConst.WRITE_OUTTIME);

                ctx.channel().write(PING_MSG);

            }
        }

        ctx.fireUserEventTriggered(evt);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        Logs.DEFAULT_LOGGER.info("Client:" + incoming.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();

    }


}

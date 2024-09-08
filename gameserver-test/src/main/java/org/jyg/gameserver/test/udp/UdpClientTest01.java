package org.jyg.gameserver.test.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.jyg.gameserver.core.startup.UdpClient;
import org.jyg.gameserver.core.util.AllUtil;

import java.net.InetSocketAddress;

/**
 * create by jiayaoguang on 2024/9/7
 */
public class UdpClientTest01 {

    private final String host;
    private final int port;

    public UdpClientTest01(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        public void initChannel(DatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
                                    // 处理接收到的数据报
                                    ByteBuf buf = packet.content();
                                    byte[] bytes = new byte[buf.readableBytes()];
                                    buf.readBytes(bytes);
                                    String received = new String(bytes, CharsetUtil.UTF_8);
                                    System.out.println("Client received: " + received);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });

            // 绑定到任意本地地址，因为我们不关心从哪里发送数据报
            Channel ch = b.bind(0).sync().channel();
            System.out.println("Client bind: " + ch.localAddress());
            // 发送数据报
            String message = "Hello from UDP Client!";
            {
                ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
                DatagramPacket packet = new DatagramPacket(buf, new InetSocketAddress(host, port));
                ch.writeAndFlush(packet);
            }
            {
                ByteBuf buf = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
                DatagramPacket packet = new DatagramPacket(buf, new InetSocketAddress(host, port));
                ch.writeAndFlush(packet);
            }
            // 等待一段时间以接收可能的响应（可选）
            // 注意：在实际应用中，你可能需要更复杂的逻辑来处理异步接收
            Thread.sleep(1000);

            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {


        {
            UdpClient udpClient = new UdpClient("127.0.0.1", 45678);
            udpClient.getGameContext().addMsgId2MsgClassMapping(1234, UdpTestMsg.class);
            udpClient.start();

            UdpTestMsg udpTestMsg = new UdpTestMsg();
            AllUtil.println(udpClient.getChannel().localAddress());

            udpClient.write(udpTestMsg);
        }
        {
            UdpClient udpClient = new UdpClient("192.168.2.230", 45678);
            udpClient.getGameContext().addMsgId2MsgClassMapping(1234, UdpTestMsg.class);
            udpClient.start();

            UdpTestMsg udpTestMsg = new UdpTestMsg();
            AllUtil.println(udpClient.getChannel().localAddress());

            udpClient.write(udpTestMsg);
        }
    }
}

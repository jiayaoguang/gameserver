package com.jyg.udp.testEcho;

import com.jyg.handle.initializer.InnerSocketServerInitializer;
import com.jyg.net.UdpService;
import com.jyg.startup.GameServerBootstarp;
import com.jyg.startup.UdpClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by jiayaoguang on 2019/7/13.
 */
public class UDPEchoClient {



    public static void main(String[] args) throws InterruptedException {

        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<Channel>(){


            @Override
            protected void initChannel(Channel channel) throws Exception {
//                channel.pipeline().addLast("StringDecoder", new StringDecoder());


                channel.pipeline().addLast("StringEncoder", new QuoteOfTheMomentClientHandler());

//                channel.pipeline().addLast("bytebufEncode", new MessageToMessageEncoder<DatagramPacket>() {
//                    @Override
//                    protected void encode(ChannelHandlerContext channelHandlerContext, DatagramPacket byteBuf, List<Object> list) throws Exception {
//
//                        list.add(byteBuf);
//                    }
//                });


            }
        };

        UdpClient client = new UdpClient();

        client.start();

        Channel channel = client.bind(9003);


        channel.writeAndFlush(new io.netty.channel.socket.DatagramPacket(
                Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
                SocketUtils.socketAddress("192.168.1.100", 9001))).sync();

        System.out.println("ojbk");

    }

}

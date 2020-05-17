package com.jyg.udp.testEcho;

import org.jyg.gameserver.core.startup.UdpClient;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

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

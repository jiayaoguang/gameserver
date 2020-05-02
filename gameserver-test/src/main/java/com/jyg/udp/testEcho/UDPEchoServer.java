package com.jyg.udp.testEcho;

import com.jyg.handle.initializer.InnerSocketServerInitializer;
import com.jyg.handle.initializer.MyChannelInitializer;
import com.jyg.net.UdpService;
import com.jyg.startup.GameServerBootstarp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.DatagramPacket;
import java.util.List;

/**
 * Created by jiayaoguang on 2019/7/13.
 */
public class UDPEchoServer {

//    public static void main(String[] args) throws InterruptedException {
//
//        UdpService udpService = new UdpService(9001 , new MyChannelInitializer<Channel>(){
//
//
//            @Override
//            protected void initChannel(Channel channel) throws Exception {
////                channel.pipeline().addLast("SimpleChannelInboundHandler", new SimpleChannelInboundHandler<DatagramPacket>() {
////
////                    @Override
////                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
////                        System.out.println("recive .... " + datagramPacket);
////                    }
////                });
//
//
////                channel.pipeline().addLast("StringDecoder", new StringDecoder());
////                channel.pipeline().addLast("StringEncoder", new StringEncoder());
//
//                channel.pipeline().addLast("hand", new QuoteOfTheMomentServerHandler());
//            }
//        });
//        udpService.start();
//
//
//    }

}

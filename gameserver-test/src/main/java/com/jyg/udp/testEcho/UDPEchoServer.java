package com.jyg.udp.testEcho;

import com.jyg.handle.initializer.InnerSocketServerInitializer;
import com.jyg.net.UdpService;
import com.jyg.startup.GameServerBootstarp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

/**
 * Created by jiayaoguang on 2019/7/13.
 */
public class UDPEchoServer {

    public static void main(String[] args){

        UdpService udpService = new UdpService(9001 , new ChannelInitializer<Channel>(){


            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast("StringDecoder", new StringDecoder());
                channel.pipeline().addLast("StringEncoder", new StringEncoder());

                channel.pipeline().addLast("hand", new MessageToMessageDecoder<String>() {
                    @Override
                    protected void decode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
                        System.out.println("recive .... " + s);
                    }
                });
            }
        });
        GameServerBootstarp gameServerBootstarp = new GameServerBootstarp();
        gameServerBootstarp.addService(udpService);
        try {
            gameServerBootstarp.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

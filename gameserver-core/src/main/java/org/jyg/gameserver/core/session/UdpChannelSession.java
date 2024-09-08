package org.jyg.gameserver.core.session;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import org.jyg.gameserver.core.data.UdpMsgInfo;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

import java.net.InetSocketAddress;

/**
 * create by jiayaoguang on 2024/9/8
 */
public class UdpChannelSession extends Session{

    private final Channel channel;

    private final String host;
    private final int port;


    public UdpChannelSession(long sessionId, Channel channel, String host, int port) {
        super(sessionId);
        this.channel = channel;
        this.host = host;
        this.port = port;
    }



	/*public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}*/



    @Override
    protected void writeObjMessage(Object msgObj) {
        UdpMsgInfo udpMsgInfo = new UdpMsgInfo(host,port,msgObj);
        this.channel.writeAndFlush(udpMsgInfo);
    }

    @Override
    public void start() {

    }


    @Override
    public void stop() {
        if(channel != null){
            try{
                channel.close();
            }catch (Exception e){
                Logs.DEFAULT_LOGGER.error("session stop exception " , e);
            }
        }
    }

    @Override
    public String getRemoteAddr(){
        return host + ":" + port;
    }

    @Override
    public boolean isOpen(){
//        if(channel == null || !channel.isActive()){
//            return false;
//        }
        return true;
    }


}

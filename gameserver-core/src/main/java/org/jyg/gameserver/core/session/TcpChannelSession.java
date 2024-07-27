package org.jyg.gameserver.core.session;

import io.netty.channel.Channel;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

public class TcpChannelSession extends Session{

    private final Channel channel;


    public TcpChannelSession(Channel channel, long sessionId){
        super(sessionId);
        this.channel = channel;
    }


	/*public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}*/

    public Channel getChannel() {
        return channel;
    }



    @Override
    protected void writeObjMessage(Object msgObj) {
        this.channel.writeAndFlush(msgObj);
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
                Logs.DEFAULT_LOGGER.error("session stop exception {}" , e);
            }
        }
    }

    @Override
    public String getRemoteAddr(){
        return AllUtil.getChannelRemoteAddr(channel);
    }

    @Override
    public boolean isOpen(){
        if(channel == null || !channel.isActive()){
            return false;
        }
        return true;
    }


}

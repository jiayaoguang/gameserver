package org.jyg.gameserver.route;

import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.startup.TcpClient;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang on 2022/8/14
 */
public class RemoteServerManager implements Lifecycle {

    private final GameContext gameContext;

    private final TcpClient tcpClient;

    public RemoteServerManager(GameContext gameContext , String remoteIp,int remotePort) {
        this.gameContext = gameContext;
        this.tcpClient = new TcpClient(gameContext , remoteIp , remotePort);
    }


    @Override
    public void start() {
        this.tcpClient.start();

        AllUtil.println("this.tcpClient.start();");
    }

    @Override
    public void stop() {

    }


    public void sendRemoteMsg(MessageLite message){

        tcpClient.checkConnect();

        if(tcpClient.isConnectAvailable()){
            tcpClient.write(message);
        }

    }


    /**
     * TODO 改为 publicEvent
     */
    public void sendRemoteMsg(ByteMsgObj message){

        tcpClient.checkConnect();

        if(tcpClient.isConnectAvailable()){
            tcpClient.write(message);
        }
    }


}

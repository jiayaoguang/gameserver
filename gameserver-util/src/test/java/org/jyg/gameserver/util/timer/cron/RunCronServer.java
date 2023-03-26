package org.jyg.gameserver.util.timer.cron;

import org.junit.Test;
import org.jyg.gameserver.core.startup.GameServerBootstrap;
import org.jyg.gameserver.core.util.AllUtil;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class RunCronServer {
    @Test
    public void testCronRunInServer(){
        GameServerBootstrap gameServerBootstrap = new GameServerBootstrap();
        gameServerBootstrap.addTcpConnector(8080);
        gameServerBootstrap.getGameContext().putInstance(GlobalCronManager.class);
        gameServerBootstrap.getGameContext().getInstance(GlobalCronManager.class).addCronTask(new MyCronTask(gameServerBootstrap.getGameContext().getMainConsumerId(),"10,20,30,40,50 * * * * ?",()->{
            AllUtil.println("------------------------");
        }));

        gameServerBootstrap.start();

        try {
            Thread.sleep(100000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

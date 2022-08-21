package org.jyg.gameserver.core.net;

import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang on 2020/5/2
 */
public abstract class AbstractConnector implements Connector {

    protected GameContext gameContext;

    protected AbstractConnector(GameContext gameContext) {
        this.gameContext = gameContext;
    }




    public GameContext getGameContext(){
        return gameContext;
    }

}

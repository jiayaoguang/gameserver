package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.event.ChannelDisconnectEvent;
import org.jyg.gameserver.core.event.GameEventListener;
import org.jyg.gameserver.core.manager.ChannelManager;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class InnerChannelDisconnectEventListener implements GameEventListener<ChannelDisconnectEvent> {

    private final ChannelManager channelManager;

    public InnerChannelDisconnectEventListener(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void onEvent(ChannelDisconnectEvent channelConnectEvent) {
        channelManager.doTcpClientUnlink(channelConnectEvent.getChannel());
    }
}

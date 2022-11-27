package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.manager.ChannelManager;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class ChannelDisconnectEventListener implements GameEventListener<ChannelDisconnectEvent>{

    private final ChannelManager channelManager;

    public ChannelDisconnectEventListener(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void onEvent(ChannelDisconnectEvent channelConnectEvent) {
        channelManager.doUnlink(channelConnectEvent.getChannel());
    }
}

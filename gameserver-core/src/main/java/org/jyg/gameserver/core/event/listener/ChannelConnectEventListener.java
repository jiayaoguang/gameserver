package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.event.ChannelConnectEvent;
import org.jyg.gameserver.core.manager.ChannelManager;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class ChannelConnectEventListener implements GameEventListener<ChannelConnectEvent> {

    private final ChannelManager channelManager;

    public ChannelConnectEventListener(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void onEvent(ChannelConnectEvent channelConnectEvent) {
        channelManager.doLink(channelConnectEvent.getChannel());
    }
}

package org.jyg.gameserver.core.event.listener;

import org.jyg.gameserver.core.event.ChannelConnectEvent;
import org.jyg.gameserver.core.event.InnerChannelConnectEvent;
import org.jyg.gameserver.core.manager.ChannelManager;

/**
 * create by jiayaoguang on 2022/11/27
 */
public class InnerChannelConnectEventListener implements GameEventListener<InnerChannelConnectEvent> {

    private final ChannelManager channelManager;

    public InnerChannelConnectEventListener(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void onEvent(InnerChannelConnectEvent channelConnectEvent) {
        channelManager.doTcpClientLink(channelConnectEvent.getChannel());
    }
}

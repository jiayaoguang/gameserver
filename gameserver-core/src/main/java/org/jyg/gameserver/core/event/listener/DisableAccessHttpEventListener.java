package org.jyg.gameserver.core.event.listener;


import org.jyg.gameserver.core.event.DisableAccessHttpEvent;

/**
 * create by jiayaoguang on 2023/5/3
 */
public class DisableAccessHttpEventListener implements GameEventListener<DisableAccessHttpEvent> {

    private final String hintText;

    public DisableAccessHttpEventListener() {
        hintText = "<h1>disable access this http path </h1>";
    }

    public DisableAccessHttpEventListener(String hintText) {
        this.hintText = hintText;
    }

    @Override
    public void onEvent(DisableAccessHttpEvent disableAccessHttpEvent) {
        disableAccessHttpEvent.getResponse().writeAndFlush(hintText);
    }
}

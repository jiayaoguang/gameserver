package org.jyg.gameserver.core.event.listener;


import com.google.protobuf.MessageLite;
import org.jyg.gameserver.core.event.ForbidAccessHttpEvent;
import org.jyg.gameserver.core.event.ForbidAccessMsgEvent;

/**
 * create by jiayaoguang on 2023/5/3
 */
public class ForbidAccessHttpEventListener implements GameEventListener<ForbidAccessHttpEvent> {

    private final String hintText;

    public ForbidAccessHttpEventListener() {
        hintText = "<h1>forbid access this http path </h1>";
    }

    public ForbidAccessHttpEventListener(String hintText) {
        this.hintText = hintText;
    }

    @Override
    public void onEvent(ForbidAccessHttpEvent forbidAccessHttpEvent) {
        forbidAccessHttpEvent.getResponse().writeAndFlush(hintText);
    }
}

package org.jyg.gameserver.core.consumer;

/**
 * create by jiayaoguang on 2021/5/23
 * Consumer 线程启动时调用的方法
 */
public interface ConsumerStartHandler {

    public void onThreadStart(GameConsumer gameConsumer);

}

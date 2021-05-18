package org.jyg.gameserver.core.consumer;

/**
 * create by jiayaoguang on 2021/5/15
 */
public interface ConsumerFactory<T extends Consumer> {

    T createConsumer();

}

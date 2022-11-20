package org.jyg.gameserver.core.consumer.choose;

import org.jyg.gameserver.core.consumer.GameConsumer;

import java.util.List;

/**
 * create by jiayaoguang on 2022/11/20
 */
public interface ChildChooser {

    GameConsumer choose(String chooseId, List<? extends GameConsumer> childConsumers);

}

package org.jyg.gameserver.core.consumer.choose;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.Event;

import java.util.List;

/**
 * create by jiayaoguang on 2022/11/20
 */
public class ModChildChooser implements ChildChooser{

    @Override
    public GameConsumer choose(String chooseId, Event event, List<? extends GameConsumer> childConsumers) {

        int childConsumerIndex = chooseId.hashCode() % childConsumers.size();

        GameConsumer childGameConsumer = childConsumers.get(childConsumerIndex);
        return childGameConsumer;
    }
}

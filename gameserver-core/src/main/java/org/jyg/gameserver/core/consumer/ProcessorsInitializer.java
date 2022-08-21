package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.processor.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2020/6/6
 */
public abstract class ProcessorsInitializer {

//    private List<Process> processList = new ArrayList<>();

    public void initProcessors(GameConsumer gameConsumer) {
        List<Processor<?>> processList = new ArrayList<>();
        initProcessors(processList);
        for (Processor<?> processor : processList) {
            gameConsumer.addProcessor(processor);
        }
    }

    public abstract void initProcessors(List<Processor<?>> processList);


}

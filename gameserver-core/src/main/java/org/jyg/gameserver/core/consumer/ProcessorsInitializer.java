package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.processor.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2020/6/6
 */
public abstract class ProcessorsInitializer {

//    private List<Process> processList = new ArrayList<>();

    public void initProcessors(Consumer consumer) {
        List<Processor<?>> processList = new ArrayList<>();
        initProcessors(processList);
        for (Processor<?> processor : processList) {
            consumer.addProcessor(processor, consumer.getContext());
        }
    }

    public abstract void initProcessors(List<Processor<?>> processList);


}

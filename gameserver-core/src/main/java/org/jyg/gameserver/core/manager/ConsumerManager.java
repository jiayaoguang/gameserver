package org.jyg.gameserver.core.manager;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.jyg.gameserver.core.consumer.Consumer;

/**
 * create by jiayaoguang on 2020/5/23
 */
public class ConsumerManager {
    private static final int DEFAULT_CONSUMER_ID = 0;
    private Int2ObjectMap<Consumer> globalQueueMap = Int2ObjectMaps.EMPTY_MAP;

    public synchronized void addConsumer(Consumer globalQueue) {
        Int2ObjectMap<Consumer> tmpGlobalQueueMap = new Int2ObjectLinkedOpenHashMap<>(this.globalQueueMap);
        tmpGlobalQueueMap.put(globalQueue.getId(), globalQueue);
        setConsumers(tmpGlobalQueueMap);
    }

    public synchronized void setConsumers(Int2ObjectMap<Consumer> globalQueueMap){
        this.globalQueueMap = Int2ObjectMaps.unmodifiable(globalQueueMap);
    }

    public void getConsumer(int id){
        this.globalQueueMap.get(id);
    }





}

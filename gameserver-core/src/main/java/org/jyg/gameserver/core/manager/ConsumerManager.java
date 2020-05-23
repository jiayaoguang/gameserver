package org.jyg.gameserver.core.manager;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.jyg.gameserver.core.util.IGlobalQueue;

/**
 * create by jiayaoguang on 2020/5/23
 */
public class ConsumerManager {
    private static final int DEFAULT_CONSUMER_ID = 0;
    private Int2ObjectMap<IGlobalQueue> globalQueueMap = Int2ObjectMaps.EMPTY_MAP;

    public synchronized void addConsumer(IGlobalQueue globalQueue) {
        Int2ObjectMap<IGlobalQueue> tmpGlobalQueueMap = new Int2ObjectLinkedOpenHashMap<>(this.globalQueueMap);
        tmpGlobalQueueMap.put(globalQueue.getId(), globalQueue);
        setConsumers(tmpGlobalQueueMap);
    }

    public synchronized void setConsumers(Int2ObjectMap<IGlobalQueue> globalQueueMap){
        this.globalQueueMap = Int2ObjectMaps.unmodifiable(globalQueueMap);
    }

    public void getConsumer(int id){
        this.globalQueueMap.get(id);
    }





}

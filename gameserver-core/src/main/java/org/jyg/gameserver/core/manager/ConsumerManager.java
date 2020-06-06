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
    private Int2ObjectMap<Consumer> consumerMap = Int2ObjectMaps.EMPTY_MAP;

    public synchronized void addConsumer(Consumer consumer) {
        Int2ObjectMap<Consumer> tmpConsumerMap = new Int2ObjectLinkedOpenHashMap<>(this.consumerMap);
        tmpConsumerMap.put(consumer.getId(), consumer);
        setConsumers(tmpConsumerMap);
    }

    public synchronized void setConsumers(Int2ObjectMap<Consumer> consumerMap){
        this.consumerMap = Int2ObjectMaps.unmodifiable(consumerMap);
    }

    public void getConsumer(int id){
        this.consumerMap.get(id);
    }





}

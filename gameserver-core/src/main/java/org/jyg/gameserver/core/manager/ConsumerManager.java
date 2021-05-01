package org.jyg.gameserver.core.manager;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.util.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2020/5/23
 */
public class ConsumerManager implements Lifecycle{
    public static final int DEFAULT_CONSUMER_ID = 0;
    private final Int2ObjectMap<Consumer> consumerMap = new Int2ObjectLinkedOpenHashMap<>();
    private final Context context;


    public ConsumerManager(Context context) {
        this.context = context;
    }


    public synchronized void addConsumer(Consumer consumer) {
        if(context.isStart()){
            throw new UnsupportedOperationException(" context.isStart , addConsumer operation fail ");
        }
        consumer.setContext(context);
        consumerMap.put(consumer.getId(), consumer);
    }

//    public synchronized void setConsumers(Int2ObjectMap<Consumer> consumerMap){
//        this.consumerMap = Int2ObjectMaps.unmodifiable(consumerMap);
//    }

    public Consumer getConsumer(int id){
        return this.consumerMap.get(id);
    }

    public Consumer getDefaultConsumer(){
        return this.consumerMap.get(DEFAULT_CONSUMER_ID);
    }

    public List<Consumer> getConsumers(){
        return new ArrayList<>(this.consumerMap.values());
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
